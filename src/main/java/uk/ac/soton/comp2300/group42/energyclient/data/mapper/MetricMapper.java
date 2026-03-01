package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;
import uk.ac.soton.comp2300.group42.metric.SaveMetricRequest;

@Mapper
public interface MetricMapper {
    Metric toMetric(MetricResponse response);
    SaveMetricRequest toSaveMetricRequest(Metric metric);
}
