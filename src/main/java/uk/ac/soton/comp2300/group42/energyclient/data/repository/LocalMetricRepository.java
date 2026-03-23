package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;

import java.util.List;
import java.util.Objects;

import static uk.ac.soton.comp2300.group42.energyclient.data.repository.LocalRepositoryUtils.throwApiException;

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
                metric.energyUsed(),
                metric.category()
        );

        data.metrics.put(newMetric.id(), newMetric);
        client.saveDataAsync();
        return newMetric;
    }

    @Override
    public Metric get(Long houseId, Long metricId) { return validateRequestExists(houseId, metricId);}

    private void validateRequestExists(Long houseId) {
        if (Objects.isNull(houseId))
            throwApiException(400, "House ID is required");

        if (!data.houses.containsKey(houseId))
            throwApiException(404, "House not found");
    }

    @Override
    public List<Metric> getAll(Long houseId) {
        validateRequestExists(houseId);

        return data.metrics.values().stream()
                .filter(a -> Objects.equals(a.houseId(), houseId))
                .toList();
    }

    @Override
    public List<Metric> getAllByCategory(Long houseId, EnergyCategory category) {
        validateRequestExists(houseId);

        return data.metrics.values().stream()
                .filter(a -> Objects.equals(a.houseId(), houseId) && Objects.equals(a.category(), category))
                .toList();
    }

    private Metric validateRequestExists(Long houseId, Long metricId) {
        validateRequestExists(metricId);

        if (Objects.isNull(metricId))
            throwApiException(400, "Metric ID is required");

        Metric metric = data.metrics.get(metricId);

        if (Objects.isNull(metric))
            throwApiException(404, "Metric not found");
        if (!Objects.equals(houseId, metric.houseId()))
            throwApiException(401, "Metric does not belong to the specified house");

        return metric;
    }

    private void validateRequestFields(Metric metric) {
        if (Objects.isNull(metric))
            throwApiException(400, "Metric is required");

        validateRequestExists(metric.houseId());

        if (Objects.isNull(metric.date()))
            throwApiException(400, "Date is required");

        if (Objects.isNull(metric.energyUsed()))
            throwApiException(400, "Energy used is required");
    }
}
