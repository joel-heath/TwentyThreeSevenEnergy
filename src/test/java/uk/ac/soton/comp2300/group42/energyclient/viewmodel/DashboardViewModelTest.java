package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.model.EnergyCalculator;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.services.NotificationService;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DashboardViewModelTest {

    @Mock EnergyCalculator mockCalc;
    @Mock ActivationRepository activationRepo;
    @Mock ApplianceRepository applianceRepo;
    @Mock NotificationService notificationService;


    @Test
    void testRecalculateCost() {
        // We tell the fake: "When someone asks to convert 100 joules, return 50.0"
        when(mockCalc.convertJoulesToPounds(100)).thenReturn(50.0);

        // 2. Inject the fake calculator into the ViewModel
        DashboardViewModel viewModel = new DashboardViewModel(mockCalc, activationRepo, applianceRepo, notificationService);

        // 3. Act: Trigger the logic
        viewModel.recalculateCost(); // (Assume this triggers logic using 100 joules)

        // 4. Assert: Check if the ViewModel updated the cost property correctly
        assertEquals("£50.00", viewModel.costProperty().get());
    }

    @Test
    void testIncrementCounter() {
        // Simple test without mocks, just checking internal state
        DashboardViewModel viewModel = new DashboardViewModel(mockCalc, activationRepo, applianceRepo, notificationService);

        assertEquals(0, viewModel.counterProperty().get());

        viewModel.incrementCounter();

        assertEquals(1, viewModel.counterProperty().get());
    }
}