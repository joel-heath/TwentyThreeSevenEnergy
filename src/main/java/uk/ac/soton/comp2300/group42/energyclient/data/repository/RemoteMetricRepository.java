package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.MetricClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.MetricMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;

import java.time.LocalDate;
import java.util.List;

@Singleton
public class RemoteMetricRepository implements MetricRepository {

    private final MetricClient client;
    private final MetricMapper mapper;

    @Inject
    public RemoteMetricRepository(MetricClient client, MetricMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Metric add(Metric metric, EnergyCategory category) {
        return mapper.toMetric(client.postMetric(metric.houseId(), mapper.toSaveMetricRequest(metric, category)));
    }

    @Override
    public Metric get(Long houseId, Long metricId) {
        return mapper.toMetric(client.fetchMetric(houseId, metricId));
    }

    @Override
    public List<Metric> getAll(Long houseId) {
        return client.fetchAllMetrics(houseId).stream()
                .map(mapper::toMetric)
                .toList();
    }

    public List<Metric> getAllByDate(Long houseId, LocalDate date) {
        return client.fetchAllMetricsByDate(houseId, date).stream()
                .map(mapper::toMetric)
                .toList();
    }

    @Override
    public List<Metric> getAllByCategory(Long houseId, EnergyCategory category) {
        return client.fetchAllMetricsByCategory(houseId, category).stream()
                .map(mapper::toMetric)
                .toList();
    }
}
