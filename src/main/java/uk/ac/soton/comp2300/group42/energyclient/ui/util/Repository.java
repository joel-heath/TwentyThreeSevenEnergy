package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;

import java.util.List;

public class Repository {

    private final ApplianceClient applianceClient;
    private final ActivationClient activationClient;
    private final NotificationService notificationService;
    private final ModelFactory modelFactory;
    private final PreferencesModel preferences;

    private final ObservableList<ApplianceModel> appliances;
    private final ObservableList<ActivationModel> activations;

    public Repository(ApplianceClient applianceClient,
                            ActivationClient activationClient,
                            NotificationService notificationService,
                            ModelFactory modelFactory) {
        this.applianceClient = applianceClient;
        this.activationClient = activationClient;
        this.notificationService = notificationService;
        this.modelFactory = modelFactory;
        this.preferences = modelFactory.getPreferencesModel(new PreferencesDTO());

        appliances = FXCollections.observableArrayList(activation -> new Observable[] { activation.nameProperty() });
        activations = FXCollections.observableArrayList(
                activation -> new Observable[] {
                        activation.activationTimeProperty(),
                        activation.applianceProperty()
                }
        );

        this.notificationService.setOnCleanupAction(this::deleteActivation);
    }

    /**
     * Primary method to fetch data from server and update UI.
     * MUST be called in this order: Appliances first, then Activations.
     */
    public void fetchAllData() {
        try {
            var applianceDTOs = applianceClient.findAll();
            List<ApplianceModel> loadedAppliances = applianceDTOs.stream()
                    .map(modelFactory::saveAppliance)
                    .toList();

            appliances.setAll(loadedAppliances);

            var activationDTOs = activationClient.findAll();
            List<ActivationModel> loadedActivations = activationDTOs.stream()
                    .map(modelFactory::createActivationModel)
                    .toList();

            activations.setAll(loadedActivations);

        } catch (Exception e) {
            // Show an Alert?
            e.printStackTrace();
        }
    }

    // --- Expose Data for ViewModels ---

    public ObservableList<ApplianceModel> getAppliances() { return appliances; }
    public ObservableList<ActivationModel> getActivations() { return activations; }
    public PreferencesModel getPreferences() { return preferences; }

    // --- Business Logic / Write Operations ---

    public void deleteActivation(ActivationModel activation) {
        try {
            activationClient.delete(activation.commit());
            notificationService.cancelNotification(activation);
            activations.remove(activation);
        } catch (Exception e) {
            // Rollback if needed, or show error
            e.printStackTrace();
        }
    }

    public void createActivation(ActivationDTO dto) {
        try {
            ActivationDTO savedDto = activationClient.save(dto);
            ActivationModel newModel = modelFactory.createActivationModel(savedDto);
            notificationService.scheduleNotification(newModel);
            if (!activations.contains(newModel))
                activations.add(newModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveActivation(ActivationModel activation) {
        try {
            ActivationDTO dto = activation.commit();
            activationClient.save(dto);
            notificationService.rescheduleNotification(activation);
            if (!activations.contains(activation))
                activations.add(activation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePreferences() {
        try {
            PreferencesDTO dto = preferences.commit();
            // call preferencesClient save
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
