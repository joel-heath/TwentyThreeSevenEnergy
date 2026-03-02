package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.google.inject.Inject;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;
import uk.ac.soton.comp2300.group42.metric.SaveMetricRequest;

import java.util.List;

public class MetricClient extends BaseApiClient {

    @Inject
    public MetricClient(AuthenticatedHttpClient httpClient, @BackendMapper JsonMapper mapper) {
        super(httpClient, mapper);
    }

    public MetricResponse postMetric(Long houseId, SaveMetricRequest request) {
        return post("houses/" + houseId + "/metrics", request, new TypeReference<>() {});
    }

    public MetricResponse fetchMetric(Long houseId, Long metricId) {
        return get("houses/" + houseId + "/metrics/" + metricId, new TypeReference<>() {});
    }

    public List<MetricResponse> fetchAllMetrics(Long houseId) {
        return get("houses/" + houseId + "/metrics", new TypeReference<>() {});
    }

    public MetricResponse putMetric(Long houseId, Long metricId, SaveMetricRequest request) {
        return put("houses/" + houseId + "/metrics/" + metricId, request, new TypeReference<>() {});
    }
}
