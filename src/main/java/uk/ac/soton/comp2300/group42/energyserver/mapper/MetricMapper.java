package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.ac.soton.comp2300.group42.energyserver.model.Metric;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;


@Mapper(componentModel = "spring")
public interface MetricMapper {
    @Mapping(source = "house.id", target = "houseId")
    MetricResponse toMetricResponse(Metric metric);
}
