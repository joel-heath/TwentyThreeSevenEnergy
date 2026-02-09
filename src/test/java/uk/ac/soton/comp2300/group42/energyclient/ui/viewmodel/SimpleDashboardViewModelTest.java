package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleDashboardViewModelTest {

    @Mock EnergyCalculator mockCalc;

    @Mock Repository mockRepo;
    PreferencesModel preferences;
    ObservableList<ActivationModel> activations;
    ObservableList<ApplianceModel> appliances;
    @Mock ActivationModel mockActivation;
    @Mock ApplianceModel mockAppliance;

    private SimpleDashboardViewModel viewModel;

    @BeforeEach void setUp() {
        preferences = new PreferencesModel(new PreferencesDTO());
        activations = FXCollections.observableArrayList();
        appliances =  FXCollections.observableArrayList(mockAppliance);

        when(mockRepo.getPreferences()).thenReturn(preferences);
        when(mockRepo.getActivations()).thenReturn(activations);
        when(mockRepo.getAppliances()).thenReturn(appliances);

        viewModel = new SimpleDashboardViewModel(mockRepo, mockCalc);
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
        assertEquals("Total Spent: £0.00", viewModel.costMessageProperty().get());
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

        assertEquals("Total Spent: £2.50", viewModel.costMessageProperty().get());

        // usage = | 2.50 spent / 1.00 goal = 2.5 | clamped to 1.0
        assertEquals(1.0, viewModel.usageProperty().get(), "Usage should update when cost updates");
    }

    @Test void testSetCostGoal() {
        mockRepo.getPreferences().setEnergyGoal(10.0);

        assertEquals("Cost Goal: £10.00", viewModel.goalMessageProperty().get());

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
        viewModel.removeActivation(mockActivation);
        verify(mockRepo).deleteActivation(mockActivation);
    }

    @Test void testUpdateActivation() {
        LocalDateTime newTime = LocalDateTime.of(2025, 1, 1, 12, 0);

        viewModel.updateActivation(mockActivation, mockAppliance, newTime);

        verify(mockActivation).setAppliance(mockAppliance);
        verify(mockActivation).setActivationTime(newTime);
        verify(mockRepo).saveActivation(mockActivation);
    }
}