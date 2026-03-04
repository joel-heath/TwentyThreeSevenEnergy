package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.service.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyUsageViewModelTest {

    @Mock private EnergyCalculator calculator;
    private ObservablePreferences preferences;
    private EnergyUsageViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "A", "B", ZoneId.systemDefault(), Role.GUEST));
        preferences = new ObservablePreferences(new Preferences(), house);
        viewModel = new EnergyUsageViewModel(calculator, preferences);
    }

    @Test
    void recalculateCost_usesCalculatorAndUpdatesCostMessage() {
        when(calculator.convertJoulesToPounds(1)).thenReturn(1.23);

        viewModel.recalculateCost();

        verify(calculator).convertJoulesToPounds(1);
        assertTrue(viewModel.costMessageProperty().get().contains("1.23"));
    }

    @Test
    void setCost_updatesCostMessageAndUsageWithClamping() {
        viewModel.setCost(0.5);
        assertEquals(0.5, viewModel.usageProperty().get(), 1e-9);

        preferences.setEnergyGoal(0.25);
        viewModel.setCost(1.0);
        assertEquals(1.0, viewModel.usageProperty().get(), 1e-9);

        preferences.setEnergyGoal(0.0);
        viewModel.setCost(1.0);
        assertEquals(0.0, viewModel.usageProperty().get(), 1e-9);
    }

    @Test
    void goalMessage_updatesWhenPreferenceGoalChanges() {
        preferences.setEnergyGoal(2.5);

        assertTrue(viewModel.goalMessageProperty().get().contains("2.50"));
    }

    @Test
    void getPreferences_returnsInjectedPreferences() {
        assertSame(preferences, viewModel.getPreferences());
    }
}
