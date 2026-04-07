package uk.ac.soton.comp2300.group42.energyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.soton.comp2300.group42.energyserver.model.EnergyPrice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnergyPriceRepository extends JpaRepository<EnergyPrice, Long> {
    Optional<EnergyPrice> findByValidFrom(LocalDateTime validFrom);
    List<EnergyPrice> findAllByValidFromBetween(LocalDateTime start, LocalDateTime end);
}
