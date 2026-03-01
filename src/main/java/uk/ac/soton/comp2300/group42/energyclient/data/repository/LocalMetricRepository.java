package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.UnauthorizedException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;

import java.util.List;
import java.util.Objects;

@Singleton
public class LocalMetricRepository implements MetricRepository {

    private final LocalStorageClient client;
    private final LocalStorageData data;

    @Inject
    public LocalMetricRepository(LocalStorageClient client) {
        this.client = client;
        this.data = client.getData();
    }

    @Override
    public Metric add(Metric metric) {
        validateRequestFields(metric);

        Metric newMetric = new Metric(
                data.nextMetricId(),
                metric.houseId(),
                metric.date(),
                metric.energyUsed()
        );

        data.metrics.put(newMetric.id(), newMetric);
        client.saveData();
        return newMetric;
    }

    @Override
    public Metric get(Long houseId, Long metricId) { return validateRequestExists(houseId, metricId);}

    private void validateRequestExists(Long houseId) {
        if (Objects.isNull(houseId))
            throw new ApiException("House ID is required", 400);

        if (!data.houses.containsKey(houseId))
            throw new ApiException("House not found", 404);
    }

    @Override
    public List<Metric> getAll(Long houseId) {
        validateRequestExists(houseId);

        return data.metrics.values().stream()
                .filter(a -> Objects.equals(a.houseId(), houseId))
                .toList();
    }

    private Metric validateRequestExists(Long houseId, Long metricId) {
        validateRequestExists(metricId);

        if (Objects.isNull(metricId))
            throw new ApiException("Metric ID is required", 400);

        Metric metric = data.metrics.get(metricId);

        if (Objects.isNull(metric))
            throw new ApiException("Metric not found", 404);
        if (!Objects.equals(houseId, metric.houseId()))
            throw new UnauthorizedException("Metric does not belong to this house");

        return metric;
    }

    private void validateRequestFields(Metric metric) {
        if (Objects.isNull(metric))
            throw new ApiException("Appliance is required", 400);

        validateRequestExists(metric.houseId());

    }
}
