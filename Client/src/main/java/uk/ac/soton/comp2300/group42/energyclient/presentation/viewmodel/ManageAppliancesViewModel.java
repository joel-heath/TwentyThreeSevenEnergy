package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;

import java.util.concurrent.Executor;

public class ManageAppliancesViewModel {

    private final ApplianceStore applianceStore;
    private final InputFeedbackManager inputFeedbackManager;
    private final Executor uiExecutor;

    private final ObservableList<ObservableAppliance> applianceList;
    private final ObjectProperty<Role> role;
    private final ObjectProperty<ObservableHouse> activeHouse;
    private final StringProperty activeHouseName;

    private final StringProperty newApplianceName = new SimpleStringProperty("");
    private final BooleanProperty hasNewApplianceError = new SimpleBooleanProperty(false);

    private final ObjectProperty<ObservableAppliance> selectedAppliance = new SimpleObjectProperty<>(null);
    private final StringProperty editApplianceName = new SimpleStringProperty("");

    @Inject public ManageAppliancesViewModel(
            ApplianceStore applianceStore,
            ObservableHousemate currentUser,
            ObservablePreferences preferences,
            InputFeedbackManager inputFeedbackManager,
            @UIExecutor Executor uiExecutor) {
        this.applianceStore = applianceStore;
        this.inputFeedbackManager = inputFeedbackManager;
        this.uiExecutor = uiExecutor;

        this.applianceList = applianceStore.getAll();
        this.role = currentUser.roleProperty();
        this.activeHouse = preferences.activeHouseProperty();
        this.activeHouseName = preferences.getActiveHouse().nameProperty();
    }

    public void loadData() {
        applianceStore.refreshAllAsync().exceptionallyAsync(ex -> {
            inputFeedbackManager.showPopup("Error loading appliances", "An error occurred while loading appliances: " + ex.getMessage());
            return null;
        }, uiExecutor);
    }

    public void addAppliance() {
        String name = newApplianceName.get() == null ? "" : newApplianceName.get().trim();

        if (name.isBlank()) {
            hasNewApplianceError.set(true);
            inputFeedbackManager.showPopup("Appliance not added", "Please enter an appliance name.");
            return;
        }

        ObservableHouse house = activeHouse.get();
        Appliance appliance = new Appliance(null, house.getId(), name);
        applianceStore.add(appliance);

        hasNewApplianceError.set(false);
        newApplianceName.set("");
        inputFeedbackManager.showPopup("Appliance added", "\"" + name + "\" has been added.");
    }

    public void selectApplianceForEdit(ObservableAppliance appliance) {
        selectedAppliance.set(appliance);
        editApplianceName.set(appliance != null ? appliance.getName() : "");
    }

    public void saveApplianceEdits() {
        ObservableAppliance current = selectedAppliance.get();
        if (current != null) {
            current.setName(editApplianceName.get().trim());
            applianceStore.update(current.commit());
            selectApplianceForEdit(null);
        }
    }

    public void deleteSelectedAppliance() {
        ObservableAppliance current = selectedAppliance.get();
        if (current != null) {
            applianceStore.delete(current.getId());
            selectApplianceForEdit(null);
        }
    }

    public StringProperty activeHouseNameProperty() { return activeHouseName; }
    public ObservableList<ObservableAppliance> getAppliances() { return applianceList; }

    public ObservableValue<Boolean> hasReadWritePermissionProperty() {
        return role.map(r -> r.getLevel() >= Role.RESIDENT.getLevel()).orElse(false);
    }

    public StringProperty newApplianceNameProperty() { return newApplianceName; }
    public BooleanProperty hasNewApplianceErrorProperty() { return hasNewApplianceError; }

    public ObjectProperty<ObservableAppliance> selectedApplianceProperty() { return selectedAppliance; }
    public StringProperty editApplianceNameProperty() { return editApplianceName; }
}