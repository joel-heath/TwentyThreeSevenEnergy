package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.AccessDeniedException;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;

@Component
public class HouseAuthorizationManager {

    private final HouseRepository houseRepo;
    private final HouseMembershipRepository membershipRepo;

    public HouseAuthorizationManager(HouseRepository houseRepo, HouseMembershipRepository membershipRepo) {
        this.houseRepo = houseRepo;
        this.membershipRepo = membershipRepo;
    }

    @Transactional(readOnly = true)
    public HouseMembership authorize(Long houseId, User user, Role minimumRole) {
        House house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House with ID " + houseId + " not found"));

        HouseMembership membership = membershipRepo.findByUserAndHouse(user, house)
                .orElseThrow(() -> new AccessDeniedException("User is not a member of this house"));

        if (membership.getRole().getLevel() < minimumRole.getLevel())
            throw new AccessDeniedException("User does not have required permissions to access this resource");

        return membership;
    }
}