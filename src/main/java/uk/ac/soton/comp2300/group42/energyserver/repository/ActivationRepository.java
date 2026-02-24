package uk.ac.soton.comp2300.group42.energyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.soton.comp2300.group42.energyserver.model.Activation;
import uk.ac.soton.comp2300.group42.energyserver.model.Appliance;
import uk.ac.soton.comp2300.group42.energyserver.model.House;

import java.util.List;
import java.util.Optional;

public interface ActivationRepository extends JpaRepository<Activation, Long> {
    List<Activation> findByAppliance_House(House applianceHouse);
    List<Activation> findByAppliance(Appliance appliance);
}
