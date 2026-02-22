package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import uk.ac.soton.comp2300.group42.energyserver.mapper.HouseMapper;
import uk.ac.soton.comp2300.group42.energyserver.mapper.HouseMembershipMapper;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.AccessDeniedException;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {

    private final HouseRepository houseRepo;
    private final HouseMembershipRepository membershipRepo;

    private final HouseMapper houseMapper;
    private final HouseMembershipMapper membershipMapper;

    public HouseService(HouseRepository houseRepo,
                        HouseMembershipRepository membershipRepo,
                        HouseMapper houseMapper,
                        HouseMembershipMapper membershipMapper) {
        this.houseRepo = houseRepo;
        this.membershipRepo = membershipRepo;
        this.houseMapper = houseMapper;
        this.membershipMapper = membershipMapper;
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

        return houseMapper.toHouseResponse(house, membership);
    }

    @Transactional
    public HouseResponse updateHouse(UpdateHouseRequest request, Long houseId, User owner) {
        House house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House with ID " + houseId + " not found"));

        HouseMembership membership = membershipRepo.findByUserAndHouse(owner, house)
                .orElseThrow(() -> new AccessDeniedException("User is not a member of this house"));

        if (membership.getRole() == Role.OWNER) {
            house.setAddress(request.address());
            house.setTimezone(request.timezone());
        }

        membership.setHouseName(request.name());

        return houseMapper.toHouseResponse(house, membership);
    }

    @Transactional(readOnly = true)
    public HousemateResponse getCurrentUserHousemate(Long houseId, User currentUser) {
        House house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House with ID " + houseId + " not found"));

        return membershipRepo.findByUserAndHouse(currentUser, house)
                .map(membershipMapper::toHousemateResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this house"));
    }

    @Transactional(readOnly = true)
    public List<HousemateResponse> getHousemates(Long houseId, User currentUser) {
        House house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House with ID " + houseId + " not found"));

        if (membershipRepo.findByUserAndHouse(currentUser, house).isEmpty())
            throw new AccessDeniedException("User is not a member of this house");

        return membershipRepo.findByHouse(house).stream()
                .map(membershipMapper::toHousemateResponse)
                .collect(Collectors.toList());
    }
}