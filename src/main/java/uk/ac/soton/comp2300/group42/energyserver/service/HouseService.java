package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;
import uk.ac.soton.comp2300.group42.energyserver.dto.HousemateResponse;
import uk.ac.soton.comp2300.group42.energyserver.exception.AccessDeniedException;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.Role;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {

    private final HouseRepository houseRepo;
    private final HouseMembershipRepository membershipRepo;
    private final UserRepository userRepo;

    public HouseService(HouseRepository houseRepo, HouseMembershipRepository membershipRepo, UserRepository userRepo) {
        this.houseRepo = houseRepo;
        this.membershipRepo = membershipRepo;
        this.userRepo = userRepo;
    }

    public House createHouse(House house, User owner) {
        House newHouse = houseRepo.save(house);

        HouseMembership membership = new HouseMembership();
        membership.setUser(owner);
        membership.setHouse(newHouse);
        membership.setRole(Role.OWNER);
        membershipRepo.save(membership);

        return newHouse;
    }

    public HousemateResponse getCurrentUserHousemate(Long houseId, User currentUser) {
        House house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House with ID " + houseId + " not found"));

        HouseMembership membership = membershipRepo.findByUserAndHouse(currentUser, house)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this house"));

        return HousemateResponse.from(membership);
    }

    public List<HousemateResponse> getHousemates(Long houseId, User currentUser) {
        House house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House with ID " + houseId + " not found"));

        if (membershipRepo.findByUserAndHouse(currentUser, house).isEmpty())
            throw new AccessDeniedException("User is not a member of this house");

        List<HouseMembership> memberships = membershipRepo.findByHouse(house);

        return memberships.stream()
                .map(HousemateResponse::from)
                .collect(Collectors.toList());
    }
}