package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

public class Repository {

    private final ApplianceClient applianceClient;
    private final ActivationClient activationClient;
    private final UserClient userClient;
    private final NotificationService notificationService;
    private final ModelFactory modelFactory;
    private final PreferencesModel preferences;

    private final ObservableList<ApplianceModel> appliances;
    private final ObservableList<ActivationModel> activations;

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
        this.preferences = modelFactory.getPreferencesModel(new PreferencesDTO());

        appliances = FXCollections.observableArrayList(activation -> new Observable[] { activation.nameProperty() });
        activations = FXCollections.observableArrayList(
                activation -> new Observable[] {
                        activation.applianceProperty(),
                        activation.activationTimeProperty(),
                        activation.activationDateProperty(),
                        activation.recursMondayProperty(),
                        activation.recursTuesdayProperty(),
                        activation.recursWednesdayProperty(),
                        activation.recursThursdayProperty(),
                        activation.recursFridayProperty(),
                        activation.recursSaturdayProperty(),
                        activation.recursSundayProperty()
                }
        );

        this.notificationService.setOnCleanupAction(this::deleteActivation);
    }

    /**
     * Primary method to fetch data from server and update UI.
     * MUST be called in this order: Appliances first, then Activations.
     */
    public void fetchAllData() {
        var applianceDTOs = applianceClient.findAll();
        List<ApplianceModel> loadedAppliances = applianceDTOs.stream()
                .map(modelFactory::saveAppliance)
                .toList();

        Platform.runLater(() -> appliances.setAll(loadedAppliances));

        var activationDTOs = activationClient.findAll();
        List<ActivationModel> loadedActivations = activationDTOs.stream()
                .map(modelFactory::createActivationModel)
                .toList();

        Platform.runLater(() -> activations.setAll(loadedActivations));    }

    // --- Expose Data for ViewModels ---

    public ObservableList<ApplianceModel> getAppliances() { return appliances; }
    public ObservableList<ActivationModel> getActivations() { return activations; }
    public PreferencesModel getPreferences() { return preferences; }

    // --- Business Logic / Write Operations ---

    public void deleteActivation(ActivationModel activation) {
        activationClient.delete(activation.commit());
        notificationService.cancelNotification(activation);
        activations.remove(activation);
    }

    public LocalDateTime createActivation(ActivationDTO dto) {
        ActivationDTO savedDto = activationClient.save(dto);
        ActivationModel newModel = modelFactory.createActivationModel(savedDto);
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
}
