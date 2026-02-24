package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.Activation;

@Mapper(componentModel = "spring")
public interface ActivationMapper {
    @Mapping(source = "appliance.id", target = "applianceId")
    ActivationResponse toActivationResponse(Activation activation);
}