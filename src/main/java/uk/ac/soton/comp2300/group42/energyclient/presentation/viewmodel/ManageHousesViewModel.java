package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HouseStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HousemateStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;

import java.time.ZoneId;
import java.util.concurrent.Executor;

public class ManageHousesViewModel {

    private final HouseStore houseStore;
    private final HousemateStore housemateStore;
    private final InputFeedbackManager inputFeedbackManager;
    private final Executor uiExecutor;

    private final ObservableList<ObservableHouse> houseList;
    private final ObservableList<ObservableHousemate> housemates;
    private final ObjectProperty<ObservableHouse> activeHouse;
    private final ObservableHousemate currentUser;
    private final ObservablePreferences preferences;

    // View State: Creating a House
    private final StringProperty newHouseName = new SimpleStringProperty("");
    private final StringProperty newHouseAddress = new SimpleStringProperty("");
    private final BooleanProperty hasNewHouseNameError = new SimpleBooleanProperty(false);
    private final BooleanProperty hasNewHouseAddressError = new SimpleBooleanProperty(false);

    // View State: Inviting
    private final StringProperty inviteEmail = new SimpleStringProperty("");
    private final BooleanProperty hasInviteEmailError = new SimpleBooleanProperty(false);

    // View State: Editing
    private final StringProperty editHouseName = new SimpleStringProperty("");
    private final StringProperty editHouseAddress = new SimpleStringProperty("");
    private final BooleanProperty isEditingHouse = new SimpleBooleanProperty(false);

    @Inject
    public ManageHousesViewModel(HouseStore houseStore,
                                 HousemateStore housemateStore,
                                 ObservableHousemate currentUser,
                                 ObservablePreferences preferences,
                                 InputFeedbackManager inputFeedbackManager,
                                 @UIExecutor Executor uiExecutor) {
        this.houseStore = houseStore;
        this.housemateStore = housemateStore;
        this.inputFeedbackManager = inputFeedbackManager;
        this.uiExecutor = uiExecutor;
        this.currentUser = currentUser;
        this.preferences = preferences;

        this.houseList = houseStore.getAll();
        this.housemates = housemateStore.getAll();
        this.activeHouse = preferences.activeHouseProperty();

        this.activeHouse.subscribe(this::updateHousemates);
    }

    public void loadData() {
        houseStore.refreshAllAsync()
            .thenCompose(_ -> housemateStore.refreshAllAsync())
            .exceptionallyAsync(e -> {
                System.err.println("Error loading house data: " + e.getMessage());
                return null;
            }, uiExecutor);
    }

    public void updateHousemates() {
        housemateStore.refreshAllAsync();
    }

    public void createHouse() {
        String name = newHouseName.get() == null ? "" : newHouseName.get().trim();
        String address = newHouseAddress.get() == null ? "" : newHouseAddress.get().trim();

        boolean error = false;
        if (name.isEmpty()) { hasNewHouseNameError.set(true); error = true; }
        else { hasNewHouseNameError.set(false); }

        if (address.isEmpty()) { hasNewHouseAddressError.set(true); error = true; }
        else { hasNewHouseAddressError.set(false); }

        if (error) {
            inputFeedbackManager.showPopup("House not created", "Please enter a house name and address before creating a new house.");
            return;
        }

        House house = new House(null, name, address, ZoneId.systemDefault(), null);
        houseStore.add(house);

        inputFeedbackManager.showPopup("House created", "Created \"" + name + "\".");
        newHouseName.set("");
        newHouseAddress.set("");
    }

    public void inviteHousemate() {
        String email = inviteEmail.get() == null ? "" : inviteEmail.get().trim();

        if (email.isEmpty()) {
            hasInviteEmailError.set(true);
            inputFeedbackManager.showPopup("Invite not sent", "Please enter an email address before sending an invite.");
            return;
        }

        Housemate housemate = new Housemate(null, getActiveHouse().getId(), "", email, Role.GUEST);
        housemateStore.invite(housemate);

        hasInviteEmailError.set(false);
        inviteEmail.set("");
        inputFeedbackManager.showPopup("Invite sent", "An invitation has been sent to " + email + ".");
    }

    public void kickHousemate(ObservableHousemate housemate) {
        housemateStore.kick(housemate.getId());
        inputFeedbackManager.showPopup("Housemate kicked", housemate.getName() + " was kicked from " + getActiveHouse().getName() + ".");
    }

    public void openEditModal() {
        ObservableHouse current = getActiveHouse();
        if (current != null) {
            editHouseName.set(current.getName());
            editHouseAddress.set(current.getAddress());
            isEditingHouse.set(true);
        }
    }

    public void saveHouseEdits() {
        ObservableHouse house = getActiveHouse();
        if (house != null) {
            house.setName(editHouseName.get().trim());
            house.setAddress(editHouseAddress.get().trim());
            houseStore.update(house.commit());
        }
        isEditingHouse.set(false);
    }

    public void closeEditModal() { isEditingHouse.set(false); }

    public void leaveActiveHouse() {
        var name = getActiveHouse().getName();
        houseStore.leave(getActiveHouse().getId());
        preferences.setActiveHouse(houseStore.getAll().getFirst());
        inputFeedbackManager.showPopup("House left", "You left " + name + ".");
    }

    public void deleteActiveHouse() {
        var name = getActiveHouse().getName();
        houseStore.delete(getActiveHouse().getId());
        preferences.setActiveHouse(houseStore.getAll().getFirst());
        inputFeedbackManager.showPopup("House deleted", "You deleted " + name + ".");
    }

    public ObservableList<ObservableHouse> getHouseList() { return houseList; }
    public ObservableList<ObservableHousemate> getHousemates() { return housemates; }
    public ObjectProperty<ObservableHouse> activeHouseProperty() { return activeHouse; }
    public ObservableHouse getActiveHouse() { return activeHouse.get(); }

    public BooleanBinding isOwnerProperty() { return currentUser.roleProperty().isEqualTo(Role.OWNER); }
    public BooleanBinding canInviteProperty() { return currentUser.roleProperty().isNotEqualTo(Role.GUEST); }

    public BooleanBinding canLeaveHouseProperty() {
        return new BooleanBinding() {
            { super.bind(currentUser.roleProperty(), housemates); }
            @Override
            protected boolean computeValue() {
                if (currentUser.getRole() != Role.OWNER) return true;
                return housemates.stream().anyMatch(h ->
                        h.getRole() == Role.OWNER && !h.getId().equals(currentUser.getId()));
            }
        };
    }

    public StringProperty newHouseNameProperty() { return newHouseName; }
    public StringProperty newHouseAddressProperty() { return newHouseAddress; }
    public BooleanProperty hasNewHouseNameErrorProperty() { return hasNewHouseNameError; }
    public BooleanProperty hasNewHouseAddressErrorProperty() { return hasNewHouseAddressError; }

    public StringProperty inviteEmailProperty() { return inviteEmail; }
    public BooleanProperty hasInviteEmailErrorProperty() { return hasInviteEmailError; }

    public StringProperty editHouseNameProperty() { return editHouseName; }
    public StringProperty editHouseAddressProperty() { return editHouseAddress; }
    public BooleanProperty isEditingHouseProperty() { return isEditingHouse; }
}