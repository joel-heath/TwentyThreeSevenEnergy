package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.energyclient.data.api.Role;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.HouseModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.HousemateModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.util.concurrent.CompletableFuture;

public class ManageHousesViewModel {

    private final Repository repository;
    private final ObservableList<HouseModel> houseList;
    private final ObservableList<HousemateModel> housemates;
    private final ObjectProperty<HouseModel> activeHouse;

    public ManageHousesViewModel(Repository repository) {
        this.repository = repository;
        this.houseList = repository.getHouses();
        this.housemates = repository.getHousemates();
        activeHouse = repository.getPreferences().activeHouseProperty();

        CompletableFuture.runAsync(repository::fetchAllData);
    }

    public Role getCurrentUserRole() { return repository.getCurrentUser().getRole(); }

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
}
