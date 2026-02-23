package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.energyclient.data.api.Role;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.HouseModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.HousemateModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.IDoEverything;

import java.util.concurrent.CompletableFuture;

public class ManageHousesViewModel {

    private final IDoEverything IDoEverything;
    private final ObservableList<HouseModel> houseList;
    private final ObservableList<HousemateModel> housemates;
    private final ObjectProperty<HouseModel> activeHouse;

    @Inject public ManageHousesViewModel(IDoEverything IDoEverything) {
        this.IDoEverything = IDoEverything;
        this.houseList = IDoEverything.getHouses();
        this.housemates = IDoEverything.getHousemates();
        activeHouse = IDoEverything.getPreferences().activeHouseProperty();

        CompletableFuture.runAsync(IDoEverything::fetchAllData);
    }

    public Role getCurrentUserRole() { return IDoEverything.getCurrentUser().getRole(); }
    public ObjectProperty<Role> currentRoleProperty() { return IDoEverything.getCurrentUser().roleProperty(); }

    public ObservableList<HouseModel> getHouseList() { return houseList; }
    public ObservableList<HousemateModel> getHousemates() { return housemates; }

    public HouseModel getActiveHouse() { return activeHouse.get(); }
    public void setActiveHouse(HouseModel house) { activeHouse.set(house); }
    public ObjectProperty<HouseModel> activeHouseProperty() { return activeHouse; }

    public void createHouse(String name, String address) {
        //repository.createHouse(name, address);
    }

    public void kickHousemate(HousemateModel housemate) {
        //repository.removeHousemate(housemate);
    }

    public void inviteHousemate(String name) {
        //repository.inviteHousemate(name);
    }

    public boolean canLeaveHouse() {
        // cannot leave a house orphaned with no owner
        return getCurrentUserRole() != Role.OWNER ||
                housemates.stream().anyMatch(h -> h.getRole() == Role.OWNER && !h.getId().equals(IDoEverything.getCurrentUser().getId()));
    }

    public void leaveActiveHouse() {
        IDoEverything.leaveActiveHouse();
    }

    public void deleteActiveHouse() {
        IDoEverything.leaveActiveHouse();
    }

    public void editActiveHouse(String name, String address) {
        activeHouse.get().setName(name);
        activeHouse.get().setAddress(address);

        // repository.saveActiveHouseEdits();

    }
}
