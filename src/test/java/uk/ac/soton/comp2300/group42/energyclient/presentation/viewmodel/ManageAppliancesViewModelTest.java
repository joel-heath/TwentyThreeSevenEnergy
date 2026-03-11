package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageAppliancesViewModelTest {

    @Mock private ApplianceStore applianceStore;
    @Mock private ObservableHousemate currentUser;
    @Mock private ObservablePreferences preferences;

    @Mock private ObservableHouse activeHouse;
    @Mock private ObservableAppliance observableAppliance;
    @Mock private Appliance committedAppliance;

    private ManageAppliancesViewModel viewModel;

    private ObjectProperty<Role> roleProperty;
    private ObservableList<ObservableAppliance> mockApplianceList;

    @BeforeEach
    void setUp() {
        roleProperty = new SimpleObjectProperty<>(Role.RESIDENT);
        ObjectProperty<ObservableHouse> activeHouseProperty = new SimpleObjectProperty<>(activeHouse);
        StringProperty activeHouseNameProperty = new SimpleStringProperty("Test House");
        mockApplianceList = FXCollections.observableArrayList();

        when(applianceStore.getAll()).thenReturn(mockApplianceList);
        when(currentUser.roleProperty()).thenReturn(roleProperty);
        when(preferences.activeHouseProperty()).thenReturn(activeHouseProperty);
        when(preferences.getActiveHouse()).thenReturn(activeHouse);
        when(activeHouse.nameProperty()).thenReturn(activeHouseNameProperty);

        viewModel = new ManageAppliancesViewModel(applianceStore, currentUser, preferences);
    }

    @Test
    void constructor_shouldFetchAllAppliancesAsynchronously() {
        verify(applianceStore, timeout(500).times(1)).refreshAllAsync();
    }

    @Test
    void getActiveHouseName_shouldReturnCorrectName() {
        assertEquals("Test House", viewModel.getActiveHouseName());
    }

    @Test
    void getAppliances_shouldReturnApplianceList() {
        assertEquals(mockApplianceList, viewModel.getAppliances());
    }

    @Test
    void createAppliance_shouldCreateAndStoreNewAppliance() {
        String applianceName = "Washing Machine";
        Long houseId = 100L;
        when(activeHouse.getId()).thenReturn(houseId);

        viewModel.createAppliance(applianceName);

        ArgumentCaptor<Appliance> applianceCaptor = ArgumentCaptor.forClass(Appliance.class);
        verify(applianceStore).add(applianceCaptor.capture());

        Appliance capturedAppliance = applianceCaptor.getValue();
        assertNull(capturedAppliance.id());
        assertEquals(houseId, capturedAppliance.houseId());
        assertEquals(applianceName, capturedAppliance.name());
    }

    @Test
    void updateAppliance_shouldUpdateObservableAndCommitToStore() {
        when(observableAppliance.commit()).thenReturn(committedAppliance);

        viewModel.updateAppliance(observableAppliance, "Dishwasher");

        verify(observableAppliance).setName("Dishwasher");
        verify(observableAppliance).commit();
        verify(applianceStore).update(committedAppliance);
    }

    @Test
    void deleteAppliance_shouldDeleteFromStoreUsingId() {
        Long applianceId = 42L;
        when(observableAppliance.getId()).thenReturn(applianceId);

        viewModel.deleteAppliance(observableAppliance);

        verify(observableAppliance).getId();
        verify(applianceStore).delete(applianceId);
    }

    @Test
    void hasReadWritePermission_whenRoleIsOwner_shouldReturnTrue() {
        roleProperty.set(Role.OWNER);
        assertTrue(viewModel.hasReadWritePermission());
    }

    @Test
    void hasReadWritePermission_whenRoleIsResident_shouldReturnTrue() {
        roleProperty.set(Role.RESIDENT);
        assertTrue(viewModel.hasReadWritePermission());
    }

    @Test
    void hasReadWritePermission_whenRoleIsBelowResident_shouldReturnFalse() {
        roleProperty.set(Role.GUEST);
        assertFalse(viewModel.hasReadWritePermission());
    }
}