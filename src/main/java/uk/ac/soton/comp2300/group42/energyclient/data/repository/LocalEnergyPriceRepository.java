package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.external.ExternalEnergyPriceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.external.UnitRateResponse;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.UnitRateMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyCost;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyPrice;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class LocalEnergyPriceRepository implements EnergyPriceRepository {

    private final ExternalEnergyPriceClient externalClient;
    private final UnitRateMapper unitRateMapper;
    private final LocalStorageClient storageClient;
    private final LocalStorageData data;

    @Inject
    public LocalEnergyPriceRepository(ExternalEnergyPriceClient externalClient, UnitRateMapper unitRateMapper, LocalStorageClient storageClient) {
        this.externalClient = externalClient;
        this.unitRateMapper = unitRateMapper;
        this.storageClient = storageClient;
        this.data = storageClient.getData();
    }

    @Override
    public List<UnitRate> fetchNext12Hours() {
        return externalClient.fetchNext12Hours()
                .stream()
                .map(unitRateMapper::toUnitRate)
                .toList();
    }

    @Override
    public List<EnergyPrice> getAllPrices() {
        return data.energyPrices.values().stream().toList();
    }

    @Override
    public List<EnergyPrice> getPricesForDate(LocalDate date) {
        return data.energyPrices.values().stream()
                .filter(price -> price.validFrom().toLocalDate().equals(date))
                .toList();
    }

    @Override
    public List<EnergyPrice> syncAndGetNext24Hours() {
        List<UnitRateResponse> externalData = externalClient.fetchNext12Hours();
        List<EnergyPrice> newPrices = externalData.stream()
                .map(this::toEnergyPrice)
                .toList();
        return saveAll(newPrices);
    }

    public List<EnergyPrice> saveAll(List<EnergyPrice> pricesToSave) {
        List<EnergyPrice> persistentPrices = new ArrayList<>();

        for (EnergyPrice p : pricesToSave) {
            EnergyPrice newPrice = new EnergyPrice(
                    p.id(),
                    p.validFrom(),
                    p.validTo(),
                    p.pricePerKwh()
            );

            data.energyPrices.put(newPrice.id(), newPrice);
            persistentPrices.add(newPrice);
        }

        storageClient.saveDataAsync();

        return persistentPrices;
    }

    @Override
    public List<EnergyCost> getCostsForDate(Long houseId, LocalDate date) {
        List<Metric> metrics = data.metrics.values().stream()
                .filter(m -> m.houseId().equals(houseId) && m.dateTime().toLocalDate().equals(date))
                .toList();

        return metrics.stream()
                .map(metric -> {
                    EnergyPrice price = findPriceForTime(metric.dateTime());
                    double cost = metric.energyUsed() * price.pricePerKwh();
                    return new EnergyCost(metric.dateTime(), metric.energyUsed(), price.pricePerKwh(), cost, metric.category());
                })
                .toList();
    }

    private EnergyPrice findPriceForTime(LocalDateTime dateTime) {
        return data.energyPrices.values().stream()
                .filter(price -> !dateTime.isBefore(price.validFrom()) && dateTime.isBefore(price.validTo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No price found for time: " + dateTime));
    }

    private EnergyPrice toEnergyPrice(UnitRateResponse raw) {
        LocalDateTime start = ZonedDateTime.parse(raw.validFrom()).toLocalDateTime();

        return new EnergyPrice(
                data.nextEnergyPriceId(),
                start,
                start.plusMinutes(30),
                raw.valueIncVat()
        );
    }
}
