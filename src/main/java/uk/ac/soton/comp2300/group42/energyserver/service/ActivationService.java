package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.activation.CreateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.UpdateActivationRequest;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.mapper.ActivationMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.*;
import uk.ac.soton.comp2300.group42.energyserver.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.ApplianceRepository;

import java.util.List;

@Service
public class ActivationService {

    private final ActivationRepository activationRepo;
    private final ApplianceRepository applianceRepo;
    private final HouseAuthorizationManager authManager;
    private final ActivationMapper mapper;

    public ActivationService(ActivationRepository activationRepo,
                             ApplianceRepository applianceRepo,
                             HouseAuthorizationManager authManager,
                             ActivationMapper mapper) {
        this.activationRepo = activationRepo;
        this.applianceRepo = applianceRepo;
        this.authManager = authManager;
        this.mapper = mapper;
    }

    @Transactional
    public ActivationResponse createActivation(Long houseId, CreateActivationRequest request, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        Appliance appliance = applianceRepo.findById(request.applianceId())
                .filter(a -> a.getHouse().equals(house))
                .orElseThrow(() -> new ResourceNotFoundException("Appliance with ID " + request.applianceId() + " not found in this house"));

        Activation activation = new Activation();
        activation.setAppliance(appliance);
        activation.setType(request.type());
        activation.setActivationTime(request.activationTime());

        if (activation.getType() == ActivationType.RECURRING) {
            activation.setRecursMonday(request.recursMonday());
            activation.setRecursTuesday(request.recursTuesday());
            activation.setRecursWednesday(request.recursWednesday());
            activation.setRecursThursday(request.recursThursday());
            activation.setRecursFriday(request.recursFriday());
            activation.setRecursSaturday(request.recursSaturday());
            activation.setRecursSunday(request.recursSunday());
        }
        else activation.setActivationDate(request.activationDate());

        activationRepo.save(activation);

        return mapper.toActivationResponse(activation);
    }

    @Transactional(readOnly = true)
    public ActivationResponse getActivationById(Long houseId, Long id, User user) {
        authManager.authorize(houseId, user, Role.GUEST);

        return activationRepo.findById(id)
                .map(mapper::toActivationResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Activation with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<ActivationResponse> getActivationsByHouseId(Long houseId, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        return activationRepo.findByAppliance_House(house)
                .stream()
                .map(mapper::toActivationResponse)
                .toList();
    }

    @Transactional
    public ActivationResponse updateActivation(Long houseId, Long id, UpdateActivationRequest request, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        Appliance appliance = applianceRepo.findById(request.applianceId())
                .filter(a -> a.getHouse().equals(house))
                .orElseThrow(() -> new ResourceNotFoundException("Appliance with ID " + request.applianceId() + " not found in this house"));

        Activation activation = activationRepo.findById(id)
                .filter(a -> a.getAppliance().getHouse().equals(house))
                .orElseThrow(() -> new ResourceNotFoundException("Activation with ID " + id + " not found in this house"));

        activation.setAppliance(appliance);
        activation.setType(request.type());
        activation.setActivationTime(request.activationTime());

        boolean isRecurring = activation.getType() == ActivationType.RECURRING;
        activation.setActivationDate(isRecurring ? null : request.activationDate());
        activation.setRecursMonday(isRecurring ? request.recursMonday() : null);
        activation.setRecursTuesday(isRecurring ? request.recursTuesday() : null);
        activation.setRecursWednesday(isRecurring ? request.recursWednesday() : null);
        activation.setRecursThursday(isRecurring ? request.recursThursday() : null);
        activation.setRecursFriday(isRecurring ? request.recursFriday() : null);
        activation.setRecursSaturday(isRecurring ? request.recursSaturday() : null);
        activation.setRecursSunday(isRecurring ? request.recursSunday() : null);

        return mapper.toActivationResponse(activation);
    }

    @Transactional
    public void deleteActivation(Long houseId, Long id, User user) {
        House house = authManager.authorize(houseId, user, Role.GUEST).getHouse();

        Activation activation = activationRepo.findById(id)
                .filter(a -> a.getAppliance().getHouse().equals(house))
                .orElseThrow(() -> new ResourceNotFoundException("Activation with ID " + id + " not found in this house"));

        activationRepo.delete(activation);
    }
}