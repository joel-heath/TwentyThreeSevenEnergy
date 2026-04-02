package uk.ac.soton.comp2300.group42.energyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Metric;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long> {
    List<Metric> findAllByHouse(House metricsHouse);
    List<Metric> findAllByHouseAndEnergyCategory(House metricsHouse, EnergyCategory energyCategory);
    List<Metric> findAllByHouseAndDateTimeBetween(House metricsHouse, LocalDateTime start, LocalDateTime end);
}
