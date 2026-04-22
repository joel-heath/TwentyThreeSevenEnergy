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
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;

import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManageAppliancesViewModelTest {

    @Mock private ApplianceStore applianceStore;
    @Mock private InputFeedbackManager inputFeedbackManager;

    private ObservableHouse house;
    private ObservableHousemate currentUser;
    private ObservableAppliance appliance;
    private ObservableList<ObservableAppliance> applianceList;
    private ManageAppliancesViewModel viewModel;

    @BeforeEach
    void setUp() {
        house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        currentUser = new ObservableHousemate(new Housemate(10L, 1L, "Alice", "alice@example.com", Role.GUEST), house);
        ObservablePreferences preferences = new ObservablePreferences(new Preferences(), house);

        appliance = new ObservableAppliance(new Appliance(100L, 1L, "Kettle"), house);
        applianceList = FXCollections.observableArrayList(appliance);

        when(applianceStore.getAll()).thenReturn(applianceList);
        when(applianceStore.refreshAllAsync()).thenReturn(CompletableFuture.completedFuture(null));

        viewModel = new ManageAppliancesViewModel(applianceStore, currentUser, preferences, inputFeedbackManager, Runnable::run);
    }

    @Test
    void hasReadWritePermissionProperty_reflectsCurrentUserRole() {
        assertFalse(viewModel.hasReadWritePermissionProperty().getValue());

        currentUser.setRole(Role.RESIDENT);
        assertTrue(viewModel.hasReadWritePermissionProperty().getValue());
    }

    @Test
    void addAppliance_whenNameBlank_setsErrorAndShowsFeedback() {
        viewModel.newApplianceNameProperty().set("   ");

        viewModel.addAppliance();

        assertTrue(viewModel.hasNewApplianceErrorProperty().get());
        verify(inputFeedbackManager).showPopup("Appliance not added", "Please enter an appliance name.");
    }

    @Test
    void addAppliance_whenValid_trimsInputAndAddsToStore() {
        viewModel.newApplianceNameProperty().set("  Toaster  ");

        viewModel.addAppliance();

        ArgumentCaptor<Appliance> captor = ArgumentCaptor.forClass(Appliance.class);
        verify(applianceStore).add(captor.capture());
        Appliance added = captor.getValue();
        assertEquals(1L, added.houseId());
        assertEquals("Toaster", added.name());
        assertFalse(viewModel.hasNewApplianceErrorProperty().get());
        assertEquals("", viewModel.newApplianceNameProperty().get());
        verify(inputFeedbackManager).showPopup("Appliance added", "\"Toaster\" has been added.");
    }

    @Test
    void selectApplianceForEdit_setsSelectionAndEditName() {
        viewModel.selectApplianceForEdit(appliance);

        assertEquals(appliance, viewModel.selectedApplianceProperty().get());
        assertEquals("Kettle", viewModel.editApplianceNameProperty().get());
    }

    @Test
    void saveApplianceEdits_updatesStoreAndClearsSelection() {
        viewModel.selectApplianceForEdit(appliance);
        viewModel.editApplianceNameProperty().set("  New Name  ");

        viewModel.saveApplianceEdits();

        ArgumentCaptor<Appliance> captor = ArgumentCaptor.forClass(Appliance.class);
        verify(applianceStore).update(captor.capture());
        assertEquals("New Name", captor.getValue().name());
        assertNull(viewModel.selectedApplianceProperty().get());
        assertEquals("", viewModel.editApplianceNameProperty().get());
    }

    @Test
    void deleteSelectedAppliance_deletesByIdAndClearsSelection() {
        viewModel.selectApplianceForEdit(appliance);

        viewModel.deleteSelectedAppliance();

        verify(applianceStore).delete(100L);
        assertNull(viewModel.selectedApplianceProperty().get());
    }

    @Test
    void loadData_whenRefreshFails_showsErrorPopup() {
        when(applianceStore.refreshAllAsync()).thenReturn(CompletableFuture.failedFuture(new RuntimeException("network")));

        viewModel.loadData();

        verify(inputFeedbackManager).showPopup("Error loading appliances", "An error occurred while loading appliances: network");
    }
}
