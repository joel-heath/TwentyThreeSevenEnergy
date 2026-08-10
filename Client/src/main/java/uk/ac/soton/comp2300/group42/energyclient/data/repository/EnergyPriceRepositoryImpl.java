package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.external.ExternalEnergyPriceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.UnitRateMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.util.List;

@Singleton
public class EnergyPriceRepositoryImpl implements EnergyPriceRepository {

    private final ExternalEnergyPriceClient externalClient;
    private final UnitRateMapper unitRateMapper;


    @Inject
    public EnergyPriceRepositoryImpl(ExternalEnergyPriceClient externalClient, UnitRateMapper unitRateMapper) {
        this.externalClient = externalClient;
        this.unitRateMapper = unitRateMapper;
    }

    @Override
    public List<UnitRate> fetchNext12Hours() {
        return externalClient.fetchNext12Hours()
                .stream()
                .map(unitRateMapper::toUnitRate)
                .toList();
    }

    @Override
    public List<UnitRate> fetchNext24Hours() {
        return externalClient.fetchNext12Hours()
                .stream()
                .map(unitRateMapper::toUnitRate)
                .toList();
    }
}