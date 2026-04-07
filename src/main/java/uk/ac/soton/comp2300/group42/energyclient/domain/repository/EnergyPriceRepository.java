package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyCost;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyPrice;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;

import java.time.LocalDate;
import java.util.List;

public interface EnergyPriceRepository {
    List<EnergyPrice> syncAndGetNext24Hours();
    List<EnergyPrice> getAllPrices();
    List<EnergyPrice> getPricesForDate(LocalDate date);
    List<UnitRate> fetchNext12Hours();
    List<EnergyCost> getCostsForDate(Long houseId, LocalDate date);
}
