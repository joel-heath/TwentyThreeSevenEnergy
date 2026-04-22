package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyUsageViewModelTest {

    @Mock private MetricRepository metricRepository;

    private ObservablePreferences preferences;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        preferences = new ObservablePreferences(new Preferences(), house);
    }

    @Test
    void constructor_recalculatesCostAndUpdatesUsageState() {
        when(metricRepository.getAllByDate(anyLong(), any(LocalDate.class))).thenReturn(List.of(
                new Metric(1L, 1L, LocalDateTime.now(), 1.0, 50.0, EnergyCategory.ELECTRICITY),
                new Metric(2L, 1L, LocalDateTime.now(), 1.0, 150.0, EnergyCategory.GAS)
        ));

        EnergyUsageViewModel viewModel = new EnergyUsageViewModel(metricRepository, preferences);

        assertTrue(viewModel.costMessageProperty().get().contains("2.00"));
        assertEquals(2.0, viewModel.usageProperty().get(), 1e-9);
        assertEquals(EnergyUsageViewModel.UsageState.CRITICAL, viewModel.usageStateProperty().get());
    }

    @Test
    void setCost_updatesUsageAndThresholdState() {
        when(metricRepository.getAllByDate(anyLong(), any(LocalDate.class))).thenReturn(List.of());
        preferences.energyGoalProperty().set(2.0);
        EnergyUsageViewModel viewModel = new EnergyUsageViewModel(metricRepository, preferences);

        viewModel.setCost(3.0);
        assertEquals(1.5, viewModel.usageProperty().get(), 1e-9);
        assertEquals(EnergyUsageViewModel.UsageState.WARNING, viewModel.usageStateProperty().get());

        viewModel.setCost(0.5);
        assertEquals(0.25, viewModel.usageProperty().get(), 1e-9);
        assertEquals(EnergyUsageViewModel.UsageState.NORMAL, viewModel.usageStateProperty().get());

        viewModel.setCost(10.0);
        assertEquals(2.0, viewModel.usageProperty().get(), 1e-9);
        assertEquals(EnergyUsageViewModel.UsageState.CRITICAL, viewModel.usageStateProperty().get());
    }

    @Test
    void goalMessage_reactsToPreferenceGoalChanges() {
        when(metricRepository.getAllByDate(anyLong(), any(LocalDate.class))).thenReturn(List.of());
        EnergyUsageViewModel viewModel = new EnergyUsageViewModel(metricRepository, preferences);

        preferences.energyGoalProperty().set(4.2);

        assertTrue(viewModel.goalMessageProperty().get().contains("4.20"));
    }

    @Test
    void preferenceProperties_areExposedByViewModel() {
        when(metricRepository.getAllByDate(anyLong(), any(LocalDate.class))).thenReturn(List.of());
        EnergyUsageViewModel viewModel = new EnergyUsageViewModel(metricRepository, preferences);

        assertSame(preferences, viewModel.getPreferences());
        assertSame(preferences.visionProperty(), viewModel.visionProperty());
    }
}

