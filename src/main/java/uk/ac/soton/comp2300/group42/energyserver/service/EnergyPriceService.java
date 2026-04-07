package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyprice.EnergyCostResponse;
import uk.ac.soton.comp2300.group42.energyprice.EnergyPriceResponse;
import uk.ac.soton.comp2300.group42.energyprice.SaveEnergyPriceRequest;
import uk.ac.soton.comp2300.group42.energyserver.mapper.EnergyPriceMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.EnergyPrice;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Metric;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.MetricRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnergyPriceService {

    private final MetricRepository metricRepo;
    private final EnergyPriceRepository priceRepo;
    private final HouseAuthorizationManager authManager;
    private final EnergyPriceMapper mapper;

    public EnergyPriceService(MetricRepository metricRepo,
                              EnergyPriceRepository priceRepo,
                              HouseAuthorizationManager authManager,
                              EnergyPriceMapper mapper) {
        this.metricRepo = metricRepo;
        this.priceRepo = priceRepo;
        this.authManager = authManager;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EnergyCostResponse> getDailyBreakdown(Long houseId, LocalDate date, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Metric> metrics = metricRepo.findAllByHouseAndDateTimeBetween(house, start, end);
        List<EnergyPrice> prices = priceRepo.findAllByValidFromBetween(start, end);

        return metrics.stream().map(metric -> {
            Double unitPrice = prices.stream()
                    .filter(p -> !metric.getDateTime().isBefore(p.getValidFrom())
                            && metric.getDateTime().isBefore(p.getValidTo()))
                    .findFirst()
                    .map(EnergyPrice::getPricePerKwh)
                    .orElse(0.0);

            double totalCost = metric.getEnergyUsed() * unitPrice;

            return new EnergyCostResponse(
                    metric.getDateTime(),
                    metric.getEnergyUsed(),
                    unitPrice,
                    totalCost
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<EnergyPriceResponse> saveAll(List<SaveEnergyPriceRequest> requests) {
        return requests.stream().map(req -> {
            EnergyPrice price = priceRepo.findByValidFrom(req.validFrom())
                                         .orElseGet(EnergyPrice::new);
            price.setValidFrom(req.validFrom());
            price.setValidTo(req.validTo());
            price.setPricePerKwh(req.priceIncVat());
            price = priceRepo.save(price);
            return mapper.toEnergyPriceResponse(price);
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnergyPriceResponse> getAllPrices() {
        return priceRepo.findAll().stream()
                .map(mapper::toEnergyPriceResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnergyPriceResponse> getPricesForDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return priceRepo.findAllByValidFromBetween(start, end).stream()
                .map(mapper::toEnergyPriceResponse)
                .collect(Collectors.toList());
    }
}
