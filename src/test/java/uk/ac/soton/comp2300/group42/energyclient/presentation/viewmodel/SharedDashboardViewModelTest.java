package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.IDoEverything;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SharedDashboardViewModelTest {

    @Mock EnergyCalculator mockCalc;

    @Mock
    IDoEverything mockRepo;
    PreferencesModel preferences;
    ObservableList<ActivationModel> activations;
    ObservableList<ApplianceModel> appliances;

    @Mock ActivationModel activation;
    @Mock ApplianceModel mockAppliance;
    @Mock HouseModel mockHouse;

    private SharedDashboardViewModel viewModel;

    @BeforeEach void setUp() {
        preferences = new PreferencesModel(new PreferencesDTO(), mockHouse);
        activations = FXCollections.observableArrayList(activation);
        appliances =  FXCollections.observableArrayList(mockAppliance);

        when(mockRepo.getPreferences()).thenReturn(preferences);
        when(mockRepo.getActivations()).thenReturn(activations);
        when(mockRepo.getAppliances()).thenReturn(appliances);

        viewModel = new SharedDashboardViewModel(mockRepo, mockCalc);
    }

    // == Initialization Tests ==
    @Test void testLoadsAppliances() {
        // Assert that during initialization the appliances list in the VM is populated from the repository
        assertEquals(1, viewModel.getAppliances().size());
        assertEquals(mockAppliance, viewModel.getAppliances().getFirst());
    }

    @Test void testDefaultValues() {
        // Verify defaults set in the field definitions
        assertEquals(0, viewModel.counterProperty().get());
        assertEquals("£0.00", viewModel.costMessageProperty().get());
        // If we add public getters for these properties:
        // assertEquals(0.0, viewModel.costValProperty().get());
        // assertEquals(1.0, viewModel.costGoalProperty().get(), "Default goal should be 1.0"); */
    }

    // == Logic Tests ==
    @Test void testIncrementCounter() {
        assertEquals(0, viewModel.counterProperty().get());
        viewModel.incrementCounter();
        assertEquals(1, viewModel.counterProperty().get());
    }

    @Test void testRecalculateCost() {
        // joules = 1 + 5 * counter
        // counter = 1 => joules = 6.
        viewModel.incrementCounter();
        when(mockCalc.convertJoulesToPounds(anyInt())).thenReturn(2.50);

        viewModel.recalculateCost();

        assertEquals("£2.50", viewModel.costMessageProperty().get());

        // usage = | 2.50 spent / 1.00 goal = 2.5 | clamped to 1.0
        assertEquals(1.0, viewModel.usageProperty().get(), "Usage should update when cost updates");
    }

    @Test void testSetCostGoal() {
        mockRepo.getPreferences().setEnergyGoal(10.0);

        assertEquals("Goal: £10.00", viewModel.goalMessageProperty().get());

        // usage = 0 current cost / 10 goal = 0 usage
        assertEquals(0.0, viewModel.usageProperty().get());
    }

    @Test void testSetCostGoalAndRecalculateCost() {
        // Spent £5.00, Goal £10.00 -> Usage should be 0.5
        mockRepo.getPreferences().setEnergyGoal(10.0);

        when(mockCalc.convertJoulesToPounds(anyInt())).thenReturn(5.0);
        viewModel.recalculateCost();

        assertEquals(0.5, viewModel.usageProperty().get(), "Usage should be 50%");
    }

    @Test void testCostClamping() {
        // Spent £20.00, Goal £10.00 -> Usage = clamp(20/10=2, 1.0) = 1.0
        mockRepo.getPreferences().setEnergyGoal(10.0);

        when(mockCalc.convertJoulesToPounds(anyInt())).thenReturn(20.0);
        viewModel.recalculateCost();

        assertEquals(1.0, viewModel.usageProperty().get(), "Usage should be clamped to max 1.0");
    }

    // == Repository Tests ==
    @Test void testRemoveActivation() {
        assertEquals(1, viewModel.getActivations().size());
        assertTrue(viewModel.getActivations().contains(activation));

        viewModel.removeActivation(activation);
        verify(mockRepo).deleteActivation(activation);

        // assertEquals(0, viewModel.getActivations().size());           // this should be true with a real repo thanks to bindings
        // assertFalse(viewModel.getActivations().contains(activation)); // but isn't with the mock.
    }

    @Test void testUpdateActivation_nonRecurring() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        LocalTime time = LocalTime.of(12, 0);

        viewModel.updateActivation(activation, mockAppliance, time, date,
                                   false, true, false, true, false, true, false,
                                   false);

        verify(activation).setAppliance(mockAppliance);
        verify(activation).setActivationTime(time);
        verify(activation).setActivationDate(date);
        verify(activation).setRecursMonday(false);
        verify(activation).setRecursTuesday(false);
        verify(activation).setRecursWednesday(false);
        verify(activation).setRecursThursday(false);
        verify(activation).setRecursFriday(false);
        verify(activation).setRecursSaturday(false);
        verify(activation).setRecursSunday(false);
        verify(mockRepo).saveActivation(activation);
    }

    @Test void testUpdateActivation_recurring() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        LocalTime time = LocalTime.of(12, 0);

        viewModel.updateActivation(activation, mockAppliance, time, date,
                false, true, false, true, false, true, false,
                true);

        verify(activation).setAppliance(mockAppliance);
        verify(activation).setActivationTime(time);
        verify(activation).setActivationDate(null);
        verify(activation).setRecursMonday(false);
        verify(activation).setRecursTuesday(true);
        verify(activation).setRecursWednesday(false);
        verify(activation).setRecursThursday(true);
        verify(activation).setRecursFriday(false);
        verify(activation).setRecursSaturday(true);
        verify(activation).setRecursSunday(false);
        verify(mockRepo).saveActivation(activation);
    }
}