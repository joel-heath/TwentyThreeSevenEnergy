package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.metrics.MetricsResponse;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.mapper.MetricsMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.MetricsRepository;


@Service
public class MetricsService {

    private final MetricsRepository metricsRepo;
    private final HouseAuthorizationManager authManager;
    private final MetricsMapper mapper;

    public MetricsService(MetricsRepository metricsRepo,
                          HouseAuthorizationManager authManager,
                          MetricsMapper mapper) {
        this.metricsRepo = metricsRepo;
        this.authManager = authManager;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public MetricsResponse getMetricsById(Long houseId, Long id, User user) {
        authManager.authorize(houseId, user, Role.GUEST);

        return metricsRepo.findById(id)
                .map(mapper::toMetricsResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance with ID " + id + " not found"));
    }
}
