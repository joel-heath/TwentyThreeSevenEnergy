package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;
import uk.ac.soton.comp2300.group42.metric.SaveMetricRequest;

public interface MetricMapper {
    Metric toMetric(MetricResponse response);
    SaveMetricRequest toSaveMetricRequest(Metric metric);
}
