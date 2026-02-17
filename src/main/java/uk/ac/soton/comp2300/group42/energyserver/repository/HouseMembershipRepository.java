package uk.ac.soton.comp2300.group42.energyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.User;

import java.util.List;
import java.util.Optional;

public interface HouseMembershipRepository extends JpaRepository<HouseMembership, Long> {
    List<HouseMembership> findByUser(User user);
    List<HouseMembership> findByHouse(House house);
    Optional<HouseMembership> findByUserAndHouse(User user, House house);
}
