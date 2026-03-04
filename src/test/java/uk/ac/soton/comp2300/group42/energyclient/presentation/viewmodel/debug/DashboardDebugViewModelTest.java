package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug;

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
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardDebugViewModelTest {

    @Mock private UserStore userStore;
    @Mock private EnergyCalculator calculator;

    private ObservablePreferences preferences;
    private DashboardDebugViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "A", "B", ZoneId.systemDefault(), Role.GUEST));
        preferences = new ObservablePreferences(new Preferences(), house);
        when(userStore.getPreferences()).thenReturn(preferences);
        viewModel = new DashboardDebugViewModel(userStore, calculator);
    }

    @Test
    void incrementCounter_recalculatesCostUsingFormula() {
        viewModel.setFormula(ignored -> 10);
        when(calculator.convertJoulesToPounds(10)).thenReturn(2.0);

        viewModel.incrementCounter();

        assertEquals(1, viewModel.counterProperty().get());
        assertEquals(2.0, viewModel.costProperty().get(), 1e-9);
        assertTrue(viewModel.costMessageProperty().get().contains("2.00"));
    }

    @Test
    void decrementCounter_doesNotGoBelowZero() {
        viewModel.setFormula(ignored -> 0);
        when(calculator.convertJoulesToPounds(0)).thenReturn(0.0);

        viewModel.decrementCounter();

        assertEquals(0, viewModel.counterProperty().get());
    }

    @Test
    void setCostGoal_updatesGoalAndMessage() {
        viewModel.setCostGoal(3.5);

        assertEquals(3.5, viewModel.goalProperty().get(), 1e-9);
        assertTrue(viewModel.goalMessageProperty().get().contains("3.50"));
    }

    @Test
    void recalculateCost_usesCustomFormulaWithCurrentCounter() {
        viewModel.counterProperty().set(5);
        viewModel.setFormula(ignored -> 42);
        when(calculator.convertJoulesToPounds(42)).thenReturn(1.5);

        viewModel.recalculateCost();

        verify(calculator).convertJoulesToPounds(42);
        assertEquals(1.5, viewModel.costProperty().get(), 1e-9);
    }

    @Test
    void scheduleReset_withPastTime_resetsImmediately() {
        viewModel.counterProperty().set(9);

        viewModel.scheduleReset(LocalDateTime.now().minusSeconds(1));

        assertEquals(0, viewModel.counterProperty().get());
    }

    @Test
    void formulaAndPreferencesAccessors_work() {
        DoubleUnaryOperator operator = x -> x + 1;
        viewModel.setFormula(operator);

        assertSame(operator, viewModel.getFormula());
        assertSame(preferences, viewModel.getPreferences());
    }

    @Test
    void save_delegatesToUserStore() {
        viewModel.save();

        verify(userStore).savePreferences();
    }
}
