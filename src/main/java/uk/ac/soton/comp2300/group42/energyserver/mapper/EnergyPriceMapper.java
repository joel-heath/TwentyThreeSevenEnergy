package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.energyprice.EnergyPriceResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.EnergyPrice;

@Mapper(componentModel = "spring")
public interface EnergyPriceMapper {
    EnergyPriceResponse toEnergyPriceResponse(EnergyPrice energyPrice);
}
