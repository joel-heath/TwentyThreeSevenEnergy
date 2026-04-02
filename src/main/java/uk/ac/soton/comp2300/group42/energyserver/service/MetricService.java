package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Metric;
import uk.ac.soton.comp2300.group42.metric.SaveMetricRequest;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.mapper.MetricMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.MetricRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class MetricService {

    private final MetricRepository metricRepo;
    private final HouseAuthorizationManager authManager;
    private final MetricMapper mapper;

    public MetricService(MetricRepository metricRepo,
                         HouseAuthorizationManager authManager,
                         MetricMapper mapper) {
        this.metricRepo = metricRepo;
        this.authManager = authManager;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public MetricResponse getMetricById(Long houseId, Long id, User user) {
        authManager.authorize(houseId, user, Role.GUEST);

        return metricRepo.findById(id)
                .map(mapper::toMetricResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Metric with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<MetricResponse> getMetricsByHouseId(Long houseId, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        return metricRepo.findAllByHouse(house)
                .stream()
                .map(mapper::toMetricResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MetricResponse> getMetricsByHouseAndCategory(Long houseId, EnergyCategory category, User user)  {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        return metricRepo.findAllByHouseAndEnergyCategory(house, category)
                .stream()
                .map(mapper::toMetricResponse)
                .toList();
    }

    public List<MetricResponse> getMetricsByHouseAndDate(Long houseId, LocalDate date, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        return metricRepo.findAllByHouseAndDateTimeBetween(house, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                .stream()
                .map(mapper::toMetricResponse)
                .toList();
    }

    @Transactional
    public MetricResponse saveMetric(Long houseId, LocalDateTime dateTime, SaveMetricRequest request, User user) {
        House house = authManager.authorize(houseId, user, Role.RESIDENT).getHouse();

        Metric metric = new Metric();
        metric.setHouse(house);
        metric.setDateTime(dateTime);
        metric.setEnergyUsed(request.energyUsed());
        metric.setEnergyCategory(request.category());
        metric = metricRepo.save(metric);

        return mapper.toMetricResponse(metric);
    }
}
