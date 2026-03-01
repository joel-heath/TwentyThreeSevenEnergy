package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;

import java.util.List;

public interface EnergyPriceRepository {
    List<UnitRate> fetchNext12Hours();
}
