package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;

import java.util.List;

public interface MetricRepository {
    Metric add(Metric metric);
    Metric get(Long houseId, Long metricId);
    List<Metric> getAll(Long houseId);
}
