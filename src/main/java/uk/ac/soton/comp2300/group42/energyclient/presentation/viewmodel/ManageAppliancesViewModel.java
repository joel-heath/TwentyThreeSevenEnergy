package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.util.concurrent.CompletableFuture;

public class ManageAppliancesViewModel {

    private final ApplianceStore applianceStore;
    private final ObservableList<ObservableAppliance> applianceList;
    private final ObjectProperty<Role> role;
    private final ObjectProperty<ObservableHouse> activeHouse;
    private final StringProperty activeHouseName;

    @Inject
    public ManageAppliancesViewModel(ApplianceStore applianceStore, ObservableHousemate currentUser, ObservablePreferences preferences) {
        this.applianceStore = applianceStore;
        this.applianceList = applianceStore.getAll();
        this.role = currentUser.roleProperty();
        this.activeHouse = preferences.activeHouseProperty();
        this.activeHouseName = preferences.getActiveHouse().nameProperty();
    }

    public CompletableFuture<Void> refreshAppliances() {
        return applianceStore.refreshAllAsync();
    }

    public String getActiveHouseName() { return activeHouseName.get(); }

    public Role getCurrentUserRole() { return role.get(); }
    public ObjectProperty<Role> currentRoleProperty() { return role; }

    public ObservableList<ObservableAppliance> getAppliances() { return applianceList; }

    public void createAppliance(String name) {
        ObservableHouse house = activeHouse.get();
        Appliance appliance = new Appliance(null, house.getId(), name);
        applianceStore.add(appliance);
    }

    public void updateAppliance(ObservableAppliance appliance, String newName) {
        appliance.setName(newName);
        applianceStore.update(appliance.commit());
    }

    public void deleteAppliance(ObservableAppliance appliance) {
        applianceStore.delete(appliance.getId());
    }

    public boolean hasReadWritePermission() {
        return getCurrentUserRole().getLevel() >= Role.RESIDENT.getLevel();
    }
}
