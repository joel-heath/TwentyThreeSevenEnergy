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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProgressTrackingViewModelTest {

    @Mock private EnergyPriceRepository energyPriceRepository;
    @Mock private MetricRepository metricRepository;
    @Mock private InputFeedbackManager inputFeedbackManager;

    private ProgressTrackingViewModel viewModel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");

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
    void initializeData_loadsAllData() {
        ZonedDateTime now = ZonedDateTime.now();
        when(energyPriceRepository.fetchNext12Hours()).thenReturn(List.of(new UnitRate(10.0, now)));

        viewModel.initializeData();

        verify(energyPriceRepository, timeout(2000)).fetchNext12Hours();
        verify(metricRepository, timeout(2000).atLeast(7)).getAllByDate(eq(1L), any(LocalDate.class));
    }

    @Test
    void loadPriceDataAsync_success_updatesUIProperties() {
        ZonedDateTime base = ZonedDateTime.of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneId.of("UTC"));
        when(energyPriceRepository.fetchNext12Hours()).thenReturn(List.of(
                new UnitRate(15.5, base),
                new UnitRate(18.0, base.plusHours(1))
        ));

        viewModel.loadPriceDataAsync();

        await(() -> viewModel.getPriceData().size() == 2);

        assertEquals("10:00", viewModel.getPriceData().getFirst().label());
        assertEquals(15.5, viewModel.getPriceData().getFirst().value().doubleValue());
        assertEquals("15.50 p/kWh", viewModel.priceLabelTextProperty().get());
    }

    @Test
    void loadWeeklyExpensesAsync_calculatesCorrectTotals() {
        LocalDate today = LocalDate.now();
        String expectedLabel = today.format(DATE_FORMATTER);

        when(metricRepository.getAllByDate(eq(1L), eq(today))).thenReturn(List.of(
                new Metric(1L, 1L, LocalDateTime.now(), 5.0, 150.0, EnergyCategory.ELECTRICITY),
                new Metric(2L, 1L, LocalDateTime.now(), 2.0, 50.0, EnergyCategory.GAS)
        ));

        viewModel.initializeData();

        await(() -> viewModel.getExpenseData().stream()
                .anyMatch(dp -> dp.label().equals(expectedLabel) && dp.value().doubleValue() == 2.0));
    }

    @Test
    void loadWeeklyUsageByCategory_filtersCorrect() {
        LocalDate today = LocalDate.now();
        String expectedLabel = today.format(DATE_FORMATTER);

        when(metricRepository.getAllByDate(eq(1L), eq(today))).thenReturn(List.of(
                new Metric(1L, 1L, LocalDateTime.now(), 10.0, 100.0, EnergyCategory.ELECTRICITY),
                new Metric(2L, 1L, LocalDateTime.now(), 5.0, 50.0, EnergyCategory.GAS)
        ));

        viewModel.initializeData();

        await(() -> viewModel.getElectricityUsageData().stream()
                .anyMatch(dp -> dp.label().equals(expectedLabel) && dp.value().doubleValue() == 10.0));

        await(() -> viewModel.getGasUsageData().stream()
                .anyMatch(dp -> dp.label().equals(expectedLabel) && dp.value().doubleValue() == 5.0));
    }

    @Test
    void logUsage_success_refreshesAndClears() {
        ZonedDateTime now = ZonedDateTime.of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneId.of("UTC"));
        when(energyPriceRepository.fetchNext12Hours()).thenReturn(List.of(new UnitRate(20.0, now)));

        viewModel.selectedCategoryProperty().set(EnergyCategory.ELECTRICITY);
        viewModel.logUsageInputProperty().set("5.0");

        viewModel.logUsage();

        ArgumentCaptor<Metric> metricCaptor = ArgumentCaptor.forClass(Metric.class);
        verify(metricRepository, timeout(2000)).add(metricCaptor.capture(), eq(EnergyCategory.ELECTRICITY));

        assertEquals(100.0, metricCaptor.getValue().energyPrice());

        await(() -> "".equals(viewModel.logUsageInputProperty().get()));
        verify(inputFeedbackManager).showPopup(eq("Success"), anyString());
    }

    private static void await(BooleanSupplier condition) {
        long timeoutAt = System.currentTimeMillis() + 3000;
        while (!condition.getAsBoolean() && System.currentTimeMillis() < timeoutAt) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted", e);
            }
        }
        if (!condition.getAsBoolean()) {
            throw new AssertionError("Condition not met within timeout");
        }
    }
}