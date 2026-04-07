package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.EnergyPriceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.external.ExternalEnergyPriceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.external.UnitRateResponse;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.EnergyPriceMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.UnitRateMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyCost;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyPrice;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyprice.SaveEnergyPriceRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Singleton
public class RemoteEnergyPriceRepository implements EnergyPriceRepository {

    private final EnergyPriceClient client;
    private final ExternalEnergyPriceClient externalClient;
    private final EnergyPriceMapper mapper;
    private final UnitRateMapper unitRateMapper;


    @Inject
    public RemoteEnergyPriceRepository(EnergyPriceClient client, ExternalEnergyPriceClient externalClient, UnitRateMapper unitRateMapper, EnergyPriceMapper mapper) {
        this.client = client;
        this.externalClient = externalClient;
        this.mapper = mapper;
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
    public List<EnergyPrice> syncAndGetNext24Hours() {
        List<UnitRateResponse> rawOctopusData = externalClient.fetchNext24Hours();

        List<SaveEnergyPriceRequest> syncRequests = rawOctopusData.stream()
                .map(raw -> {
                    LocalDateTime start = ZonedDateTime.parse(raw.validFrom()).toLocalDateTime();
                    LocalDateTime end = start.plusMinutes(30);
                    return new SaveEnergyPriceRequest(start, end, raw.valueIncVat());
                })
                .toList();

        return client.postEnergyPrices(syncRequests).stream()
                .map(mapper::toEnergyPrice)
                .toList();
    }

    @Override
    public List<EnergyPrice> getPricesForDate(LocalDate date) {
        return client.fetchEnergyPricesForDate(date).stream()
                .map(mapper::toEnergyPrice)
                .toList();
    }

    @Override
    public List<EnergyPrice> getAllPrices() {
        return client.fetchAllEnergyPrices().stream()
                .map(mapper::toEnergyPrice)
                .toList();
    }

    @Override
    public List<EnergyCost> getCostsForDate(Long houseId, LocalDate date) {
        return client.fetchEnergyCostsForDate(houseId, date).stream()
                .map(mapper::toEnergyCost)
                .toList();
    }
}