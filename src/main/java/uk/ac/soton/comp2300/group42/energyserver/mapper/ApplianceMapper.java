package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.Appliance;

@Mapper(componentModel = "spring")
public interface ApplianceMapper {
    ApplianceResponse toApplianceResponse(Appliance appliance);
}