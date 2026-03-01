package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug.DashboardDebugViewModel;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardDebugViewModelTest {


    @Mock
    IDoEverything mockRepo;
    ObservablePreferences preferences;
    @Mock EnergyCalculator calc;
    @Mock
    ObservableHouse mockHouse;

    private DashboardDebugViewModel vm;

    @BeforeEach void setUp() {
        preferences = new ObservablePreferences(new PreferencesDTO(), mockHouse);
        when(mockRepo.getPreferences()).thenReturn(preferences);

        vm = new DashboardDebugViewModel(mockRepo, calc);
    }

    @Test
    void testDefaultValues() {
        assertEquals(0, vm.counterProperty().getValue());
        assertEquals(0, vm.usageProperty().getValue());
        assertEquals(0, vm.costProperty().getValue());
        assertEquals("Total Spent: £0.00", vm.costMessageProperty().getValue());
        assertEquals(1, vm.goalProperty().getValue());
        assertEquals("Goal: £1.00", vm.goalMessageProperty().getValue());
    }

    @Test
    void testIncrementCounter() {
        assertEquals(0, vm.counterProperty().getValue());
        assertEquals("Total Spent: £0.00", vm.costMessageProperty().getValue());
        vm.incrementCounter();
        assertEquals(1, vm.counterProperty().getValue());
    }

    @Test
    void testIncrementThenDecrementCounter() {
        assertEquals(0, vm.counterProperty().getValue());
        vm.incrementCounter();
        vm.incrementCounter();
        vm.decrementCounter();
        assertEquals(1, vm.counterProperty().getValue());
    }

    @Test
    void testDecrementZero() {
        vm.decrementCounter();
        assertEquals(0, vm.counterProperty().getValue());
    }

    @Test
    void testSetGoal() {
        assertEquals(1, vm.goalProperty().getValue());
        vm.setCostGoal(0.5);
        assertEquals(0.5, vm.goalProperty().getValue());
    }

    @Test
    void testApplyFormula() {
        vm.incrementCounter();
        int originalVal = (int) vm.getFormula().applyAsDouble(vm.counterProperty().get());
        assertEquals(600, originalVal);
        vm.incrementCounter();
        int updatedVal = (int) vm.getFormula().applyAsDouble(vm.counterProperty().get());
        assertEquals(1100, updatedVal);
    }

    @Test
    void testSetFormula() {
        vm.incrementCounter();
        int originalVal = (int) vm.getFormula().applyAsDouble(vm.counterProperty().get());
        assertEquals(600, originalVal);
        vm.setFormula(x -> 200*x);
        int updatedVal = (int) vm.getFormula().applyAsDouble(vm.counterProperty().get());
        assertEquals(200, updatedVal);
    }

}
