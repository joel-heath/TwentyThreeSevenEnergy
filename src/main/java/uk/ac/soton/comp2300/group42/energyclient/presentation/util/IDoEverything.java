package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

// Why has this been called "IDoEverything"?
// As it stands this is a "God Object", an anti-pattern.
// It was previously called Repository, but it also contains business logic and orchestration of notifications.
// TODO: Separate Concerns and refactor into smaller classes with single responsibilities.
// Concerns that should be separated:
// - ActivationsViewModel that fetches all Appliances and Activations and exposes them as ObservableLists.
// - CurrentUserViewModel that fetches the current user and their preferences and exposes them as properties
// - HousesViewModel that fetches all houses and housemates for the active house, exposes them as ObservableLists
// - Orchestrator of activations and notifications, that listens to changes in activations and preferences and updates notifications accordingly.
//   this could alternatively be implemented with an event bus pattern.
// Until we refactor this, use this as the single source of truth.
// (i.e. IDoEverything).

@Singleton
public class IDoEverything {

    private final ApplianceRepository applianceRepo;
    private final ActivationRepository activationRepo;
    private final UserRepository userRepo;
    private final HouseRepository houseRepo;
    private final NotificationService notificationService;
    private final ModelFactory modelFactory;
    private final PreferencesModel preferences;
    private final HousemateModel currentUser;

    private final ObservableList<ApplianceModel> appliances;
    private final ObservableList<ActivationModel> activations;
    private final ObservableList<HouseModel> houses;
    private final ObservableList<HousemateModel> housemates;

    @Inject
    public IDoEverything(ApplianceRepository applianceRepo,
                         ActivationRepository activationRepo,
                         UserRepository userRepo,
                         HouseRepository houseRepo,
                         NotificationService notificationService,
                         ModelFactory modelFactory) {
        this.applianceRepo = applianceRepo;
        this.activationRepo = activationRepo;
        this.userRepo = userRepo;
        this.houseRepo = houseRepo;
        this.notificationService = notificationService;
        this.modelFactory = modelFactory;
        this.preferences = modelFactory.getPreferencesModel();
        this.currentUser = modelFactory.getCurrentUser();
        ColorVisionManager.bind(preferences.visionProperty());

        appliances = FXCollections.observableArrayList();
        houses = FXCollections.observableArrayList();
        housemates = FXCollections.observableArrayList();
        activations = FXCollections.observableArrayList(
                activation -> new Observable[] {
                        activation.activationTimeProperty(),
                        activation.activationDateProperty(),
                        activation.recursMondayProperty(),
                        activation.recursTuesdayProperty(),
                        activation.recursWednesdayProperty(),
                        activation.recursThursdayProperty(),
                        activation.recursFridayProperty(),
                        activation.recursSaturdayProperty(),
                        activation.recursSundayProperty(),
                        activation.updateTriggerProperty()
                }
        );

        preferences.activeHouseProperty().subscribe(newHouse -> {
            if (newHouse == null) return;
            Housemate me = houseRepo.getCurrentUserAsHousemate(newHouse.getId());
            Platform.runLater(() -> currentUser.updateFrom(me, newHouse));
        });
        currentUser.houseProperty().subscribe(newHouse -> {
            if (newHouse != null && !newHouse.equals(preferences.getActiveHouse()))
                Platform.runLater(() -> preferences.setActiveHouse(newHouse));
        });

        this.notificationService.setOnCleanupAction(this::deleteActivation);

        CompletableFuture.runAsync(() -> {
            var preferencesResponse = userRepo.getCurrentPreferences();
            House houseEntity;
            try { houseEntity = houseRepo.get(preferencesResponse.activeHouseId()); }
            catch (ApiException e) {
                var others = houseRepo.getCurrentUserHouses();
                houseEntity = others.isEmpty()
                        ? houseRepo.add()
                        : others.getFirst();
            }
            var houseModel = modelFactory.getHouseModel(houseEntity);
            Platform.runLater(() -> preferences.updateFrom(preferencesResponse, houseModel));
        });
    }

    private <DTO, Model> void buildAndSet(List<DTO> dtos, Function<DTO, Model> modelBuilder, ObservableList<Model> targetList) {
        Platform.runLater(() -> {
            var models = dtos.stream().map(modelBuilder).toList();
            targetList.setAll(models);
        });
    }

    /**
     * Primary method to fetch data from server and update UI.
     * MUST be called in this order:
     * 0. (Constructor: fetch preferences and active house)
     * 1. Houses       Appliances
     * 2. Housemates   Activations
     */
    public void fetchAllData() {
        var houseDTOs = houseRepo.getCurrentUserHouses();
        buildAndSet(houseDTOs, modelFactory::getHouseModel, houses);

        var preferencesDomainModel = userRepo.getCurrentPreferences();
        Platform.runLater(() -> {
            HouseModel activeHouse = houses.stream()
                    .filter(h -> Objects.equals(h.getId(), preferencesDomainModel.activeHouseId()))
                    .findFirst().orElse(null);
            assert activeHouse != null;
            this.preferences.updateFrom(preferencesDomainModel, activeHouse);
        });

        var housemateDTOs = houseRepo.getHousemates(preferences.getActiveHouse().getId());
        buildAndSet(housemateDTOs, modelFactory::getHousemateModel, housemates);

        var applianceDTOs = applianceRepo.getAll(preferences.getActiveHouse().getId());
        buildAndSet(applianceDTOs, modelFactory::getApplianceModel, appliances);

        var activationModels = activationRepo.getAll(preferences.getActiveHouse().getId());
        buildAndSet(activationModels, modelFactory::getActivationModel, activations);
    }

    // --- Expose Data for ViewModels ---

    public HousemateModel getCurrentUser() { return currentUser; }
    public PreferencesModel getPreferences() { return preferences; }
    public ObservableList<HouseModel> getHouses() { return houses; }
    public ObservableList<HousemateModel> getHousemates() { return housemates; }
    public ObservableList<ApplianceModel> getAppliances() { return appliances; }
    public ObservableList<ActivationModel> getActivations() { return activations; }

    // --- Business Logic / Write Operations ---

    public void deleteActivation(ActivationModel activation) {
        activationRepo.delete(activation.getAppliance().getHouse().getId(), activation.getId());
        notificationService.cancelNotification(activation);
        activations.remove(activation);
    }

    public LocalDateTime createActivation(Activation domainModel) {
        Activation savedDto = activationRepo.add(domainModel);
        ActivationModel newModel = modelFactory.getActivationModel(savedDto);
        LocalDateTime activationTime = notificationService.scheduleNotification(newModel);
        if (!activations.contains(newModel))
            activations.add(newModel);
        return activationTime;
    }

    public void saveActivation(ActivationModel activation) {
        Activation domainModel = activation.commit();
        activationRepo.update(domainModel);
        notificationService.rescheduleNotification(activation);
        if (!activations.contains(activation))
            activations.add(activation);
    }

    public void savePreferences() {
        Preferences domainModel = preferences.commit(currentUser.getId());
        // TODO: call preferencesClient save
    }

    // User repo requires no caching of UserDTO objects so no need to wrap methods, just expose it as is.
    public UserRepository getUserRepo() { return userRepo; }

    public void leaveActiveHouse() {
        // TODO: houseRepo.leaveHouse(preferences.getActiveHouse().getId());

        var nextHouse = houseRepo.getCurrentUserHouses().getFirst();
        var houseModel = modelFactory.getHouseModel(nextHouse);
        preferences.setActiveHouse(houseModel);
    }

    public void deleteActiveHouse() {
        houseRepo.delete(preferences.getActiveHouse().getId());

        var nextHouse = houseRepo.getCurrentUserHouses().getFirst();
        var houseModel = modelFactory.getHouseModel(nextHouse);
        preferences.setActiveHouse(houseModel);
    }
}
