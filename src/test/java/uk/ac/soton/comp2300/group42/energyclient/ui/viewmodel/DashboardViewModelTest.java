package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardViewModelTest {

    @Mock ModelFactory modelFactory;
    @Mock EnergyCalculator mockCalc;
    @Mock ActivationClient activationClient;
    @Mock ApplianceClient applianceClient;
    @Mock NotificationService notificationService;

    @Mock ActivationDTO mockActivationDTO;
    @Mock ActivationModel mockActivation;
    @Mock ApplianceDTO mockApplianceDTO;
    @Mock ApplianceModel mockAppliance;

    private uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.DashboardViewModel viewModel;

    @BeforeEach void setUp() {
        when(activationClient.findAll()).thenReturn(List.of());
        when(applianceClient.findAll()).thenReturn(List.of(mockApplianceDTO));
        viewModel = new DashboardViewModel(modelFactory, mockCalc, activationClient, applianceClient, notificationService);
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
        viewModel.setCostGoal(10.00);

        assertEquals("Cost Goal: £10.00", viewModel.goalMessageProperty().get());

        // usage = 0 current cost / 10 goal = 0 usage
        assertEquals(0.0, viewModel.usageProperty().get());
    }

    @Test void testSetCostGoalAndRecalculateCost() {
        // Spent £5.00, Goal £10.00 -> Usage should be 0.5
        viewModel.setCostGoal(10.0);

        when(mockCalc.convertJoulesToPounds(anyInt())).thenReturn(5.0);
        viewModel.recalculateCost();

        assertEquals(0.5, viewModel.usageProperty().get(), "Usage should be 50%");
    }

    @Test void testCostClamping() {
        // Spent £20.00, Goal £10.00 -> Usage = clamp(20/10=2, 1.0) = 1.0
        viewModel.setCostGoal(10.0);

        when(mockCalc.convertJoulesToPounds(anyInt())).thenReturn(20.0);
        viewModel.recalculateCost();

        assertEquals(1.0, viewModel.usageProperty().get(), "Usage should be clamped to max 1.0");
    }

    // == Repository Tests ==
    @Test void testRemoveActivation() {
        viewModel.removeActivation(mockActivation);
        // verify(activationClient).delete(mockActivation.commit()); this would produce a different object

        // assert no such element with .getId() == mockActivation.getId() in activationClient.findAll()
    }

    @Test void testUpdateActivation() {
        LocalDateTime newTime = LocalDateTime.of(2025, 1, 1, 12, 0);

        viewModel.updateActivation(mockActivation, mockAppliance, newTime);

        verify(mockActivation).setAppliance(mockAppliance);
        verify(mockActivation).setActivationTime(newTime);
        // verify(activationClient).save(mockActivation);
        // verify(notificationService).rescheduleNotification(mockActivation);
    }
}