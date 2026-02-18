package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Singleton
public class Repository {

    private final ApplianceClient applianceClient;
    private final ActivationClient activationClient;
    private final UserClient userClient;
    private final NotificationService notificationService;
    private final ModelFactory modelFactory;
    private final PreferencesModel preferences;
    private final HousemateModel currentUser;

    private final ObservableList<ApplianceModel> appliances;
    private final ObservableList<ActivationModel> activations;
    private final ObservableList<HouseModel> houses;
    private final ObservableList<HousemateModel> housemates;

    @Inject
    public Repository(ApplianceClient applianceClient,
                      ActivationClient activationClient,
                      UserClient userClient,
                      NotificationService notificationService,
                      ModelFactory modelFactory) {
        this.applianceClient = applianceClient;
        this.activationClient = activationClient;
        this.userClient = userClient;
        this.notificationService = notificationService;
        this.modelFactory = modelFactory;
        this.preferences = modelFactory.getPreferencesModel();
        this.currentUser = modelFactory.getCurrentUser();

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
            userClient.findCurrentUserByHouseId(newHouse.getId()).ifPresent(meDTO ->
                    Platform.runLater(() -> currentUser.updateFrom(meDTO, newHouse))
            );
        });
        currentUser.houseProperty().subscribe(newHouse -> {
            if (newHouse != null && !newHouse.equals(preferences.getActiveHouse()))
                Platform.runLater(() -> preferences.setActiveHouse(newHouse));
        });

        this.notificationService.setOnCleanupAction(this::deleteActivation);

        CompletableFuture.runAsync(() -> {
            var preferencesDTO = userClient.findPreferences();
            var houseDTO = userClient.findHouseById(preferencesDTO.getActiveHouseId()).orElseGet(() -> {
                var others = userClient.findHousesForCurrentUser();
                return others.isEmpty()
                        ? userClient.createDefaultHouse()
                        : others.getFirst();
            });
            var houseModel = modelFactory.getHouseModel(houseDTO);
            preferencesDTO.setActiveHouseId(houseDTO.getId());
            Platform.runLater(() -> preferences.updateFrom(preferencesDTO, houseModel));
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
        var houseDTOs = userClient.findHousesForCurrentUser();
        buildAndSet(houseDTOs, modelFactory::getHouseModel, houses);

        var housemateDTOs = userClient.findAllByHouseId(preferences.getActiveHouse().getId());
        buildAndSet(housemateDTOs, modelFactory::getHousemateModel, housemates);

        var applianceDTOs = applianceClient.findAll(preferences.getActiveHouse().getId());
        buildAndSet(applianceDTOs, modelFactory::getApplianceModel, appliances);

        var activationModels = activationClient.findAll(preferences.getActiveHouse().getId());
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
        activationClient.delete(activation.commit());
        notificationService.cancelNotification(activation);
        activations.remove(activation);
    }

    public LocalDateTime createActivation(ActivationDTO dto) {
        ActivationDTO savedDto = activationClient.save(dto);
        ActivationModel newModel = modelFactory.getActivationModel(savedDto);
        LocalDateTime activationTime = notificationService.scheduleNotification(newModel);
        if (!activations.contains(newModel))
            activations.add(newModel);
        return activationTime;
    }

    public void saveActivation(ActivationModel activation) {
        ActivationDTO dto = activation.commit();
        activationClient.save(dto);
        notificationService.rescheduleNotification(activation);
        if (!activations.contains(activation))
            activations.add(activation);
    }

    public void savePreferences() {
        PreferencesDTO dto = preferences.commit();
        // call preferencesClient save
    }

    // User client requires no caching of UserDTO objects so no need to wrap methods, just expose it as is.
    public UserClient getUserClient() { return userClient; }

    public void leaveActiveHouse() {
        // userClient.leaveHouse(preferences.getActiveHouse().getId());

        var others = userClient.findHousesForCurrentUser();
        var houseDTO = others.isEmpty()
                ? userClient.createDefaultHouse()
                : others.getFirst();
        var houseModel = modelFactory.getHouseModel(houseDTO);
        preferences.setActiveHouse(houseModel);
    }

    public void deleteActiveHouse() {
        // userClient.deleteHouse(preferences.getActiveHouse().getId());

        leaveActiveHouse();
    }
}
