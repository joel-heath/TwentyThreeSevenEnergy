package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;

import java.time.LocalDate;
import java.util.List;

public interface MetricRepository {
    Metric add(Metric metric, EnergyCategory category);
    Metric get(Long houseId, Long metricId);
    List<Metric> getAll(Long houseId);
    List<Metric> getAllByDate(Long houseId, LocalDate date);
    List<Metric> getAllByCategory(Long houseId, EnergyCategory category);
    List<Double> getAllCostsByDate(Long houseId, LocalDate date);
}
