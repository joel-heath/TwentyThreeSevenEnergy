package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import uk.ac.soton.comp2300.group42.energyserver.mapper.HouseMembershipMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.Preferences;
import uk.ac.soton.comp2300.group42.energyserver.repository.PreferencesRepository;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {

    private final HouseRepository houseRepo;
    private final HouseMembershipRepository membershipRepo;
    private final PreferencesRepository preferencesRepo;
    private final HouseAuthorizationManager authManager;
    private final HouseMembershipMapper mapper;

    public HouseService(HouseRepository houseRepo,
                        HouseMembershipRepository membershipRepo,
                        PreferencesRepository preferencesRepo,
                        HouseAuthorizationManager authManager,
                        HouseMembershipMapper mapper) {
        this.houseRepo = houseRepo;
        this.membershipRepo = membershipRepo;
        this.preferencesRepo = preferencesRepo;
        this.authManager = authManager;
        this.mapper = mapper;
    }

    @Transactional
    public HouseResponse createHouse(CreateHouseRequest request, User owner) {
        House house = new House();
        house.setAddress(request.address());
        house.setTimezone(request.timezone());
        house = houseRepo.save(house);

        HouseMembership membership = new HouseMembership();
        membership.setUser(owner);
        membership.setHouse(house);
        membership.setRole(Role.OWNER);
        membership.setHouseName(request.name());
        membershipRepo.save(membership);

        return mapper.toHouseResponse(membership);
    }

    @Transactional(readOnly = true)
    public HouseResponse getHouseById(Long houseId, User user) {
        HouseMembership membership = authManager.authorize(houseId, user, Role.OWNER);

        return mapper.toHouseResponse(membership);
    }

    @Transactional
    public HouseResponse updateHouse(Long houseId, UpdateHouseRequest request, User user) {
        HouseMembership membership = authManager.authorize(houseId, user, Role.OWNER);
        House house = membership.getHouse();

        if (membership.getRole() == Role.OWNER) {
            house.setAddress(request.address());
            house.setTimezone(request.timezone());
        }

        membership.setHouseName(request.name());

        return mapper.toHouseResponse(membership);
    }

    @Transactional
    public void deleteHouse(Long houseId, User user) {
        House house = authManager.authorize(houseId, user, Role.OWNER).getHouse();

        for (Preferences prefs : preferencesRepo.findByActiveHouse(house)) {
            User affectedUser = prefs.getUser();
            List<HouseMembership> otherMemberships = membershipRepo.findByUserAndHouseNot(affectedUser, house);

            if (!otherMemberships.isEmpty())
                prefs.setActiveHouse(otherMemberships.getFirst().getHouse());
            else {
                House newDefaultHouse = new House();
                newDefaultHouse.setAddress("No Address Set");
                newDefaultHouse.setTimezone(ZoneId.of("UTC"));
                newDefaultHouse = houseRepo.save(house);

                HouseMembership newMembership = new HouseMembership();
                newMembership.setUser(affectedUser);
                newMembership.setHouse(newDefaultHouse);
                newMembership.setRole(Role.OWNER);
                newMembership.setHouseName("Default House");
                membershipRepo.save(newMembership);

                prefs.setActiveHouse(newDefaultHouse);
            }
        }

        houseRepo.delete(house);
    }

    @Transactional(readOnly = true)
    public List<HouseResponse> getHousesByUser(User user) {
        return membershipRepo.findByUser(user)
                .stream()
                .map(mapper::toHouseResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HousemateResponse getUserAsHousemate(Long houseId, User currentUser) {
        HouseMembership membership = authManager.authorize(houseId, currentUser, Role.GUEST);

        return mapper.toHousemateResponse(membership);
    }

    @Transactional(readOnly = true)
    public List<HousemateResponse> getHousemates(Long houseId, User currentUser) {
        House house = authManager.authorize(houseId, currentUser, Role.GUEST).getHouse();

        return membershipRepo.findByHouse(house).stream()
                .map(mapper::toHousemateResponse)
                .collect(Collectors.toList());
    }
}