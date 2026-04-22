package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProgressTrackingViewModelTest {

    @Mock private EnergyPriceRepository energyPriceRepository;
    @Mock private MetricRepository metricRepository;
    @Mock private InputFeedbackManager inputFeedbackManager;

    private ProgressTrackingViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        ObservablePreferences preferences = new ObservablePreferences(new Preferences(), house);
        when(metricRepository.getAllByDate(anyLong(), any(LocalDate.class))).thenReturn(List.of());

        viewModel = new ProgressTrackingViewModel(
                energyPriceRepository,
                metricRepository,
                preferences,
                inputFeedbackManager,
                Runnable::run
        );
    }

    @Test
    void loadPriceDataAsync_success_populatesChartAndLabel() {
        ZonedDateTime base = ZonedDateTime.of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneId.of("UTC"));
        when(energyPriceRepository.fetchNext12Hours()).thenReturn(List.of(
                new UnitRate(15.5, base),
                new UnitRate(18.0, base.plusHours(1))
        ));

        viewModel.loadPriceDataAsync();

        await(() -> viewModel.getPriceData().size() == 2);
        assertEquals("10:00", viewModel.getPriceData().get(0).label());
        assertEquals(15.5, viewModel.getPriceData().get(0).value().doubleValue(), 1e-9);
        assertEquals("15.50 p/kWh", viewModel.priceLabelTextProperty().get());
        assertEquals("", viewModel.priceLabelStyleClassProperty().get());
    }

    @Test
    void loadPriceDataAsync_failure_setsErrorLabel() {
        when(energyPriceRepository.fetchNext12Hours()).thenThrow(new RuntimeException("down"));

        viewModel.loadPriceDataAsync();

        await(() -> "Failed to load data.".equals(viewModel.priceLabelTextProperty().get()));
        assertEquals("response-error", viewModel.priceLabelStyleClassProperty().get());
    }

    @Test
    void logUsage_whenInputEmpty_showsValidationFeedback() {
        viewModel.logUsageInputProperty().set("   ");

        viewModel.logUsage();

        verify(inputFeedbackManager).showPopup("Invalid Input", "Please enter a value to log.");
        verify(metricRepository, never()).add(any(Metric.class), any(EnergyCategory.class));
    }

    @Test
    void logUsage_whenInputNonNumeric_showsValidationFeedback() {
        viewModel.logUsageInputProperty().set("abc");

        viewModel.logUsage();

        verify(inputFeedbackManager).showPopup("Invalid Input", "Please enter a valid numeric value.");
        verify(metricRepository, never()).add(any(Metric.class), any(EnergyCategory.class));
    }

    @Test
    void logUsage_whenValid_persistsMetricClearsInputAndShowsSuccess() {
        ZonedDateTime now = ZonedDateTime.of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneId.of("UTC"));
        when(energyPriceRepository.fetchNext12Hours()).thenReturn(List.of(new UnitRate(20.0, now)));
        when(metricRepository.add(any(Metric.class), eq(EnergyCategory.GAS))).thenAnswer(invocation -> invocation.getArgument(0));

        viewModel.selectedCategoryProperty().set(EnergyCategory.GAS);
        viewModel.logUsageInputProperty().set("2.5");
        viewModel.logUsage();

        ArgumentCaptor<Metric> metricCaptor = ArgumentCaptor.forClass(Metric.class);
        verify(metricRepository, timeout(1000)).add(metricCaptor.capture(), eq(EnergyCategory.GAS));
        Metric metric = metricCaptor.getValue();
        assertEquals(1L, metric.houseId());
        assertEquals(2.5, metric.energyUsed(), 1e-9);
        assertEquals(50.0, metric.energyPrice(), 1e-9);
        assertEquals(EnergyCategory.GAS, metric.category());

        await(() -> "".equals(viewModel.logUsageInputProperty().get()));
        verify(inputFeedbackManager, timeout(1000)).showPopup("Success", "Logged 2.5 kWh.");
    }

    private static void await(BooleanSupplier condition) {
        long timeoutAt = System.currentTimeMillis() + 2000;
        while (!condition.getAsBoolean() && System.currentTimeMillis() < timeoutAt) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted while waiting for async condition", e);
            }
        }
        assertTrue(condition.getAsBoolean(), "Condition did not become true before timeout");
    }
}
