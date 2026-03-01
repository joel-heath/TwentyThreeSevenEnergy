package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HouseStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HousemateStore;

import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

public class ManageHousesViewModel {

    private final HouseStore houseStore;
    private final HousemateStore housemateStore;
    private final ObservableList<ObservableHouse> houseList;
    private final ObservableList<ObservableHousemate> housemates;
    private final ObjectProperty<ObservableHouse> activeHouse;
    private final ObservableHousemate currentUser;
    private final ObservablePreferences preferences;

    @Inject
    public ManageHousesViewModel(HouseStore houseStore, HousemateStore housemateStore, ObservableHousemate currentUser, ObservablePreferences preferences) {
        this.houseStore = houseStore;
        this.housemateStore = housemateStore;
        this.houseList = houseStore.getAll();
        this.housemates = housemateStore.getAll();
        this.activeHouse = preferences.activeHouseProperty();
        this.currentUser = currentUser;
        this.preferences = preferences;

        CompletableFuture.runAsync(() -> {
            houseStore.refreshAll();
            housemateStore.refreshAll();
        });
    }

    public Role getCurrentUserRole() { return currentUser.getRole(); }
    public ObjectProperty<Role> currentRoleProperty() { return currentUser.roleProperty(); }

    public ObservableList<ObservableHouse> getHouseList() { return houseList; }
    public ObservableList<ObservableHousemate> getHousemates() { return housemates; }

    public ObservableHouse getActiveHouse() { return activeHouse.get(); }
    public void setActiveHouse(ObservableHouse house) { activeHouse.set(house); }
    public ObjectProperty<ObservableHouse> activeHouseProperty() { return activeHouse; }

    public void createHouse(String name, String address) {
        House house = new House(null, name, address, ZoneId.systemDefault(), null);
        houseStore.add(house);
    }

    public void kickHousemate(ObservableHousemate housemate) {
        housemateStore.kick(housemate.getId());
    }

    public void inviteHousemate(String email) {
        Housemate housemate = new Housemate(null, getActiveHouse().getId(), "", email, Role.GUEST);
        housemateStore.invite(housemate);
    }

    public boolean canLeaveHouse() {
        // cannot leave a house orphaned with no owner
        return getCurrentUserRole() != Role.OWNER ||
                housemates.stream().anyMatch(h ->
                        h.getRole() == Role.OWNER &&
                        !h.getId().equals(currentUser.getId()));
    }

    public void leaveActiveHouse() {
        houseStore.leave(activeHouse.get().getId());

        var nextHouse = houseStore.getAll().getFirst();
        preferences.setActiveHouse(nextHouse);
    }

    public void deleteActiveHouse() {
        houseStore.delete(activeHouse.get().getId());

        var nextHouse = houseStore.getAll().getFirst();
        preferences.setActiveHouse(nextHouse);
    }

    public void editActiveHouse(String name, String address) {
        ObservableHouse house = activeHouse.get();
        house.setName(name);
        house.setAddress(address);

        houseStore.update(house.commit());
    }
}
