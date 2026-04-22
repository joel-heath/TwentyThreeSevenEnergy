package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.service.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardDebugViewModelTest {

    @Mock private UserStore userStore;
    @Mock private EnergyCalculator energyCalculator;

    private ObservablePreferences preferences;
    private DashboardDebugViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        preferences = new ObservablePreferences(new Preferences(), house);

        when(userStore.getPreferences()).thenReturn(preferences);
        when(energyCalculator.convertJoulesToPounds(anyInt())).thenAnswer(invocation -> invocation.<Integer>getArgument(0) / 100.0);

        viewModel = new DashboardDebugViewModel(userStore, energyCalculator, Runnable::run);
    }

    @Test
    void incrementAndDecrement_updateCounterAndCost() {
        viewModel.incrementCounter();
        assertEquals(1, viewModel.counterProperty().get());
        assertEquals(6.0, viewModel.costProperty().get(), 1e-9);
        assertTrue(viewModel.costMessageProperty().get().contains("6.00"));

        viewModel.decrementCounter();
        assertEquals(0, viewModel.counterProperty().get());
        assertEquals(1.0, viewModel.costProperty().get(), 1e-9);
    }

    @Test
    void updateCostGoal_whenValid_updatesGoalAndClearsInput() {
        viewModel.costGoalInputProperty().set("3.5");

        viewModel.updateCostGoal();

        assertEquals(3.5, viewModel.goalProperty().get(), 1e-9);
        assertTrue(viewModel.goalMessageProperty().get().contains("3.50"));
        assertFalse(viewModel.hasCostGoalErrorProperty().get());
        assertEquals("", viewModel.costGoalInputProperty().get());
    }

    @Test
    void updateCostGoal_whenInvalid_setsErrorFlag() {
        viewModel.costGoalInputProperty().set("invalid");

        viewModel.updateCostGoal();

        assertTrue(viewModel.hasCostGoalErrorProperty().get());
    }

    @Test
    void scheduleReset_whenTimeIsInPast_resetsImmediately() {
        viewModel.incrementCounter();
        assertEquals(1, viewModel.counterProperty().get());

        viewModel.resetDateProperty().set(LocalDate.now().minusDays(1));
        viewModel.resetTimeProperty().set(LocalTime.now().withSecond(0).withNano(0));
        viewModel.scheduleReset();

        assertEquals(0, viewModel.counterProperty().get());
    }

    @Test
    void resetCounter_setsCounterToZeroAndRecalculatesCost() {
        viewModel.incrementCounter();
        viewModel.incrementCounter();
        assertEquals(2, viewModel.counterProperty().get());

        viewModel.resetCounter();

        assertEquals(0, viewModel.counterProperty().get());
        assertEquals(1.0, viewModel.costProperty().get(), 1e-9);
    }

    @Test
    void save_delegatesToUserStore() {
        viewModel.save();
        verify(userStore).savePreferences();
    }

    @Test
    void formulaAndVisionProperties_areExposed() {
        DoubleUnaryOperator formula = x -> x * 42;
        viewModel.setFormula(formula);

        assertSame(formula, viewModel.getFormula());
        assertSame(preferences.visionProperty(), viewModel.visionProperty());
        assertIterableEquals(Arrays.asList(ColorVision.values()), viewModel.getAvailableVisions());
    }
}
