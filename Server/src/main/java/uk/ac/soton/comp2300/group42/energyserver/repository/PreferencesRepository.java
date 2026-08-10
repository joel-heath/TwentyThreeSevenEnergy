package uk.ac.soton.comp2300.group42.energyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Preferences;
import uk.ac.soton.comp2300.group42.energyserver.model.User;

import java.util.List;

public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
    Preferences findByUser(User user);
    List<Preferences> findByActiveHouse(House activeHouse);
}
