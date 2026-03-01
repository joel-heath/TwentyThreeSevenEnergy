package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.external.EnergyPriceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.UnitRateMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.util.List;

@Singleton
public class EnergyPriceRepositoryImpl implements EnergyPriceRepository {

    private final EnergyPriceClient client;
    private final UnitRateMapper mapper;

    @Inject
    public EnergyPriceRepositoryImpl(EnergyPriceClient client, UnitRateMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public List<UnitRate> fetchNext12Hours() {
        return client.fetchNext12Hours()
                .stream()
                .map(mapper::toUnitRate)
                .toList();
    }
}
