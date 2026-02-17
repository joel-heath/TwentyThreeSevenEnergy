package uk.ac.soton.comp2300.group42.energyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.soton.comp2300.group42.energyserver.model.House;

public interface HouseRepository extends JpaRepository<House, Long> {
}
