package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.appliance.CreateApplianceRequest;
import uk.ac.soton.comp2300.group42.appliance.UpdateApplianceRequest;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.mapper.ApplianceMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.Appliance;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.ApplianceRepository;

import java.util.List;

@Service
public class ApplianceService {

    private final ApplianceRepository applianceRepo;
    private final HouseAuthorizationManager authManager;
    private final ApplianceMapper mapper;

    public ApplianceService(ApplianceRepository applianceRepo,
                            HouseAuthorizationManager authManager,
                            ApplianceMapper mapper) {
        this.applianceRepo = applianceRepo;
        this.authManager = authManager;
        this.mapper = mapper;
    }

    @Transactional
    public ApplianceResponse createAppliance(Long houseId, CreateApplianceRequest request, User user) {
        House house = authManager.authorize(houseId, user, Role.RESIDENT).getHouse();

        Appliance appliance = new Appliance();
        appliance.setHouse(house);
        appliance.setName(request.name());
        appliance = applianceRepo.save(appliance);

        return mapper.toApplianceResponse(appliance);
    }

    @Transactional(readOnly = true)
    public ApplianceResponse getApplianceById(Long houseId, Long id, User user) {
        authManager.authorize(houseId, user, Role.GUEST);

        return applianceRepo.findById(id)
                .map(mapper::toApplianceResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<ApplianceResponse> getAppliancesByHouseId(Long houseId, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        return applianceRepo.findAllByHouse(house)
                .stream()
                .map(mapper::toApplianceResponse)
                .toList();
    }

    @Transactional
    public ApplianceResponse updateAppliance(Long houseId, Long id, UpdateApplianceRequest request, User user) {
        authManager.authorize(houseId, user, Role.RESIDENT);

        Appliance appliance = applianceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance with ID " + id + " not found"));

        appliance.setName(request.name());

        return mapper.toApplianceResponse(appliance);
    }

    @Transactional
    public void deleteAppliance(Long houseId, Long id, User user) {
        authManager.authorize(houseId, user, Role.RESIDENT);

        Appliance appliance = applianceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance with ID " + id + " not found"));

        applianceRepo.delete(appliance);
    }
}