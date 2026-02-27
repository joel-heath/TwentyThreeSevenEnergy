package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.ac.soton.comp2300.group42.energyserver.model.Metrics;
import uk.ac.soton.comp2300.group42.metrics.MetricsResponse;


@Mapper(componentModel = "spring")
public interface MetricsMapper {
    @Mapping(source = "house.id", target = "houseId")
    MetricsResponse toMetricsResponse(Metrics metrics);
}
