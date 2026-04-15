package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeAll;
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
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressTrackingViewModelTest {

    @Mock private EnergyPriceRepository repository;
    @Mock private MetricRepository metricRepository;

    private ObservablePreferences preferences;
    private ProgressTrackingViewModel viewModel;

    @BeforeAll
    static void initJavaFx() {
        JavaFxTestUtil.initJavaFx();
    }

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(9L, "H", "A", ZoneId.systemDefault(), Role.GUEST));
        preferences = new ObservablePreferences(new Preferences(), house);
        viewModel = new ProgressTrackingViewModel(repository, metricRepository, preferences);
    }

    @Test
    void loadDataAsync_populatesPriceSeriesAndCurrentPrice() {
        List<UnitRate> rates = List.of(
                new UnitRate(8.5, ZonedDateTime.of(2026, 3, 4, 12, 0, 0, 0, ZoneOffset.UTC)),
                new UnitRate(9.0, ZonedDateTime.of(2026, 3, 4, 13, 0, 0, 0, ZoneOffset.UTC))
        );
        when(repository.fetchNext12Hours()).thenReturn(rates);

        viewModel.loadDataAsync().join();
        JavaFxTestUtil.waitForFxEvents();

        assertEquals(8.5, viewModel.currentPriceProperty().get(), 1e-9);
        assertEquals(1, viewModel.getPriceSeriesData().size());
        assertEquals(2, viewModel.getPriceSeriesData().getFirst().getData().size());
        assertEquals("12:00", viewModel.getPriceSeriesData().getFirst().getData().get(0).getXValue());
        assertEquals(9.0, viewModel.getPriceSeriesData().getFirst().getData().get(1).getYValue());
    }

    @Test
    void loadMockExpenses_populatesSevenDaySeriesFromMetrics() {
        List<Metric> metrics = List.of(
                new Metric(1L, 9L, LocalDate.now(), 1.0, EnergyCategory.OTHER),
                new Metric(2L, 9L, LocalDate.now(), 2.0, EnergyCategory.ELECTRICITY),
                new Metric(3L, 9L, LocalDate.now(), 3.0, EnergyCategory.GAS),
                new Metric(4L, 9L, LocalDate.now(), 4.0, EnergyCategory.OTHER),
                new Metric(5L, 9L, LocalDate.now(), 5.0, EnergyCategory.GAS),
                new Metric(6L, 9L, LocalDate.now(), 6.0, EnergyCategory.ELECTRICITY),
                new Metric(7L, 9L, LocalDate.now(), 7.0, EnergyCategory.OTHER)
        );
        when(metricRepository.getAll(9L)).thenReturn(metrics);

        viewModel.loadMockExpenses();

        assertEquals(1, viewModel.getExpenseSeriesData().size());
        assertEquals(7, viewModel.getExpenseSeriesData().getFirst().getData().size());
        assertEquals(1.0, viewModel.getExpenseSeriesData().getFirst().getData().get(0).getYValue());
        assertEquals(7.0, viewModel.getExpenseSeriesData().getFirst().getData().get(6).getYValue());
    }
}
