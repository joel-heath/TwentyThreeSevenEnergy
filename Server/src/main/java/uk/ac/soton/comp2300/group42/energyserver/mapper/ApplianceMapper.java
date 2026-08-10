package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.Appliance;

@Mapper(componentModel = "spring")
public interface ApplianceMapper {
    @Mapping(source = "house.id", target = "houseId")
    ApplianceResponse toApplianceResponse(Appliance appliance);
}