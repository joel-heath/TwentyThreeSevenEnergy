package uk.ac.soton.comp2300.group42.energyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
//import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Metric;

//import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long> {
    //List<Double> findByMetricsHouse(House metricsHouse);
}
