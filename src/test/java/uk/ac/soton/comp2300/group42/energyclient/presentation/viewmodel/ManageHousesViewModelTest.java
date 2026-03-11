package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HouseStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.HousemateStore;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageHousesViewModelTest {

    @Mock private HouseStore houseStore;
    @Mock private HousemateStore housemateStore;
    @Mock private ObservableHousemate currentUser;
    @Mock private ObservablePreferences preferences;
    @Mock private ObservableHouse activeHouse;

    private ObservableList<ObservableHouse> houseList;
    private ObservableList<ObservableHousemate> housemates;
    private ObjectProperty<ObservableHouse> activeHouseProperty;

    private ManageHousesViewModel viewModel;

    @BeforeEach
    void setUp() {
        houseList = FXCollections.observableArrayList();
        housemates = FXCollections.observableArrayList();
        activeHouseProperty = new SimpleObjectProperty<>(activeHouse);

        when(houseStore.getAll()).thenReturn(houseList);
        when(housemateStore.getAll()).thenReturn(housemates);
        when(preferences.activeHouseProperty()).thenReturn(activeHouseProperty);

        viewModel = new ManageHousesViewModel(houseStore, housemateStore, currentUser, preferences);
    }

    @Test
    void constructor_initialisesListsAndStartsAsyncRefresh() {
        assertSame(houseList, viewModel.getHouseList());
        assertSame(housemates, viewModel.getHousemates());

        verify(houseStore, timeout(1000)).refreshAllAsync();
        verify(housemateStore, timeout(1000)).refreshAllAsync();
    }

    @Test
    void activeHouse_accessors_usePreferenceProperty() {
        ObservableHouse otherHouse = new ObservableHouse(new House(2L, "House", "Addr", ZoneId.systemDefault(), Role.GUEST));
        viewModel.setActiveHouse(otherHouse);

        assertSame(otherHouse, viewModel.getActiveHouse());
        assertSame(activeHouseProperty, viewModel.activeHouseProperty());
    }

    @Test
    void role_accessors_delegateToCurrentUser() {
        ObjectProperty<Role> roleProperty = new SimpleObjectProperty<>(Role.OWNER);
        when(currentUser.getRole()).thenReturn(Role.OWNER);
        when(currentUser.roleProperty()).thenReturn(roleProperty);

        assertSame(Role.OWNER, viewModel.getCurrentUserRole());
        assertSame(roleProperty, viewModel.currentRoleProperty());
    }

    @Test
    void createHouse_buildsDomainObjectAndDelegates() {
        viewModel.createHouse("Home", "Road");

        ArgumentCaptor<House> captor = ArgumentCaptor.forClass(House.class);
        verify(houseStore).add(captor.capture());
        House created = captor.getValue();
        assertEquals(null, created.id());
        assertEquals("Home", created.name());
        assertEquals("Road", created.address());
        assertTrue(created.timezone() != null);
    }

    @Test
    void kickHousemate_delegatesById() {
        ObservableHousemate housemate = org.mockito.Mockito.mock(ObservableHousemate.class);
        when(housemate.getId()).thenReturn(77L);

        viewModel.kickHousemate(housemate);

        verify(housemateStore).kick(77L);
    }

    @Test
    void inviteHousemate_usesActiveHouseIdAndGuestRole() {
        when(activeHouse.getId()).thenReturn(55L);

        viewModel.inviteHousemate("x@example.com");

        ArgumentCaptor<Housemate> captor = ArgumentCaptor.forClass(Housemate.class);
        verify(housemateStore).invite(captor.capture());
        Housemate invited = captor.getValue();
        assertEquals(55L, invited.houseId());
        assertEquals("x@example.com", invited.email());
        assertEquals(Role.GUEST, invited.role());
    }

    @Test
    void canLeaveHouse_returnsTrueForNonOwners() {
        when(currentUser.getRole()).thenReturn(Role.GUEST);

        assertTrue(viewModel.canLeaveHouse());
    }

    @Test
    void canLeaveHouse_returnsFalseForSoleOwner() {
        when(currentUser.getRole()).thenReturn(Role.OWNER);
        when(currentUser.getId()).thenReturn(1L);
        ObservableHousemate onlyOwner = org.mockito.Mockito.mock(ObservableHousemate.class);
        when(onlyOwner.getRole()).thenReturn(Role.OWNER);
        when(onlyOwner.getId()).thenReturn(1L);
        housemates.setAll(onlyOwner);

        assertFalse(viewModel.canLeaveHouse());
    }

    @Test
    void canLeaveHouse_returnsTrueWhenAnotherOwnerExists() {
        when(currentUser.getRole()).thenReturn(Role.OWNER);
        when(currentUser.getId()).thenReturn(1L);
        ObservableHousemate otherOwner = org.mockito.Mockito.mock(ObservableHousemate.class);
        when(otherOwner.getRole()).thenReturn(Role.OWNER);
        when(otherOwner.getId()).thenReturn(2L);
        housemates.setAll(otherOwner);

        assertTrue(viewModel.canLeaveHouse());
    }

    @Test
    void leaveActiveHouse_callsStoreAndSetsNextActiveHouse() {
        ObservableHouse nextHouse = org.mockito.Mockito.mock(ObservableHouse.class);
        houseList.setAll(nextHouse);
        when(activeHouse.getId()).thenReturn(10L);

        viewModel.leaveActiveHouse();

        verify(houseStore).leave(10L);
        verify(preferences).setActiveHouse(nextHouse);
    }

    @Test
    void deleteActiveHouse_callsStoreAndSetsNextActiveHouse() {
        ObservableHouse nextHouse = org.mockito.Mockito.mock(ObservableHouse.class);
        houseList.setAll(nextHouse);
        when(activeHouse.getId()).thenReturn(11L);

        viewModel.deleteActiveHouse();

        verify(houseStore).delete(11L);
        verify(preferences).setActiveHouse(nextHouse);
    }

    @Test
    void editActiveHouse_updatesFieldsAndPersistsCommit() {
        ObservableHouse realHouse = new ObservableHouse(new House(3L, "Old", "OldAddr", ZoneId.systemDefault(), Role.OWNER));
        activeHouseProperty.set(realHouse);

        viewModel.editActiveHouse("NewName", "NewAddr");

        verify(houseStore).update(any(House.class));
        assertEquals("NewName", realHouse.getName());
        assertEquals("NewAddr", realHouse.getAddress());
    }
}
