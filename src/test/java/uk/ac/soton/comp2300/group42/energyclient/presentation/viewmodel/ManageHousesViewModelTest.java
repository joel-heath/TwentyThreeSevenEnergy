package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HouseStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HousemateStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;

import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManageHousesViewModelTest {

    @Mock private HouseStore houseStore;
    @Mock private HousemateStore housemateStore;
    @Mock private InputFeedbackManager inputFeedbackManager;

    private ObservableHouse activeHouse;
    private ObservableHouse fallbackHouse;
    private ObservableHousemate currentUser;
    private ObservableList<ObservableHouse> houses;
    private ObservableList<ObservableHousemate> housemates;
    private ObservablePreferences preferences;
    private ManageHousesViewModel viewModel;

    @BeforeEach
    void setUp() {
        activeHouse = new ObservableHouse(new House(1L, "Main House", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        fallbackHouse = new ObservableHouse(new House(2L, "Backup House", "2 Street", ZoneId.of("UTC"), Role.RESIDENT));
        currentUser = new ObservableHousemate(new Housemate(10L, 1L, "Alice", "alice@example.com", Role.OWNER), activeHouse);

        houses = FXCollections.observableArrayList(fallbackHouse);
        housemates = FXCollections.observableArrayList(currentUser);

        preferences = new ObservablePreferences(new Preferences(), activeHouse);

        when(houseStore.getAll()).thenReturn(houses);
        when(houseStore.refreshAllAsync()).thenReturn(CompletableFuture.completedFuture(null));
        when(housemateStore.getAll()).thenReturn(housemates);
        when(housemateStore.refreshAllAsync()).thenReturn(CompletableFuture.completedFuture(null));

        viewModel = new ManageHousesViewModel(
                houseStore,
                housemateStore,
                currentUser,
                preferences,
                inputFeedbackManager,
                Runnable::run
        );
    }

    @Test
    void loadData_refreshesHousesThenHousemates() {
        viewModel.loadData();

        verify(houseStore).refreshAllAsync();
        verify(housemateStore).refreshAllAsync();
    }

    @Test
    void createHouse_whenFieldsBlank_setsErrorsAndShowsFeedback() {
        viewModel.newHouseNameProperty().set(" ");
        viewModel.newHouseAddressProperty().set(" ");

        viewModel.createHouse();

        assertTrue(viewModel.hasNewHouseNameErrorProperty().get());
        assertTrue(viewModel.hasNewHouseAddressErrorProperty().get());
        verify(inputFeedbackManager).showPopup("House not created", "Please enter a house name and address before creating a new house.");
    }

    @Test
    void createHouse_whenValid_trimsInputAndAddsHouse() {
        viewModel.newHouseNameProperty().set("  New House ");
        viewModel.newHouseAddressProperty().set("  99 Lane ");

        viewModel.createHouse();

        ArgumentCaptor<House> captor = ArgumentCaptor.forClass(House.class);
        verify(houseStore).add(captor.capture());
        House created = captor.getValue();
        assertEquals("New House", created.name());
        assertEquals("99 Lane", created.address());
        assertEquals("", viewModel.newHouseNameProperty().get());
        assertEquals("", viewModel.newHouseAddressProperty().get());
        verify(inputFeedbackManager).showPopup("House created", "Created \"New House\".");
    }

    @Test
    void inviteHousemate_whenEmailBlank_setsErrorAndShowsFeedback() {
        viewModel.inviteEmailProperty().set(" ");

        viewModel.inviteHousemate();

        assertTrue(viewModel.hasInviteEmailErrorProperty().get());
        verify(inputFeedbackManager).showPopup("Invite not sent", "Please enter an email address before sending an invite.");
    }

    @Test
    void inviteHousemate_whenValid_invitesGuestAndClearsInput() {
        viewModel.inviteEmailProperty().set("  bob@example.com ");

        viewModel.inviteHousemate();

        ArgumentCaptor<Housemate> captor = ArgumentCaptor.forClass(Housemate.class);
        verify(housemateStore).invite(captor.capture());
        Housemate invited = captor.getValue();
        assertEquals(1L, invited.houseId());
        assertEquals("bob@example.com", invited.email());
        assertEquals(Role.GUEST, invited.role());
        assertFalse(viewModel.hasInviteEmailErrorProperty().get());
        assertEquals("", viewModel.inviteEmailProperty().get());
        verify(inputFeedbackManager).showPopup("Invite sent", "An invitation has been sent to bob@example.com.");
    }

    @Test
    void openEditModalAndSaveHouseEdits_updatesHouseAndClosesModal() {
        viewModel.openEditModal();
        assertTrue(viewModel.isEditingHouseProperty().get());
        assertEquals("Main House", viewModel.editHouseNameProperty().get());
        assertEquals("1 Street", viewModel.editHouseAddressProperty().get());

        viewModel.editHouseNameProperty().set("  Updated House ");
        viewModel.editHouseAddressProperty().set("  Updated Address ");
        viewModel.saveHouseEdits();

        ArgumentCaptor<House> captor = ArgumentCaptor.forClass(House.class);
        verify(houseStore).update(captor.capture());
        House saved = captor.getValue();
        assertEquals("Updated House", saved.name());
        assertEquals("Updated Address", saved.address());
        assertFalse(viewModel.isEditingHouseProperty().get());
    }

    @Test
    void leaveActiveHouse_leavesAndSwitchesToFirstAvailableHouse() {
        viewModel.leaveActiveHouse();

        verify(houseStore).leave(1L);
        assertEquals(fallbackHouse, preferences.getActiveHouse());
        verify(inputFeedbackManager).showPopup("House left", "You left Main House.");
    }

    @Test
    void deleteActiveHouse_deletesAndSwitchesToFirstAvailableHouse() {
        preferences.setActiveHouse(activeHouse);

        viewModel.deleteActiveHouse();

        verify(houseStore).delete(1L);
        assertEquals(fallbackHouse, preferences.getActiveHouse());
        verify(inputFeedbackManager).showPopup("House deleted", "You deleted Main House.");
    }

    @Test
    void canLeaveHouseProperty_requiresSecondOwnerWhenCurrentUserIsOwner() {
        assertFalse(viewModel.canLeaveHouseProperty().get());

        housemates.add(new ObservableHousemate(
                new Housemate(20L, 1L, "Bob", "bob@example.com", Role.OWNER),
                activeHouse
        ));
        assertTrue(viewModel.canLeaveHouseProperty().get());

        currentUser.setRole(Role.RESIDENT);
        assertTrue(viewModel.canLeaveHouseProperty().get());
    }
}
