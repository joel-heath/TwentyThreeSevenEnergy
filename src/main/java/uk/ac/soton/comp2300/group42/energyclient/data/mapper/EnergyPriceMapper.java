package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyCost;
import uk.ac.soton.comp2300.group42.energyprice.EnergyCostResponse;
import uk.ac.soton.comp2300.group42.energyprice.EnergyPriceResponse;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyPrice;

@Mapper
public interface EnergyPriceMapper {
    EnergyPrice toEnergyPrice(EnergyPriceResponse response);
    EnergyCost toEnergyCost(EnergyCostResponse response);
}
