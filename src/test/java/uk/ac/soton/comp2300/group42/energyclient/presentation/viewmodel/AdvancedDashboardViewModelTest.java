package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvancedDashboardViewModelTest {

    @Mock private EnergyPriceRepository repository;

    @BeforeAll
    static void initJavaFx() {
        JavaFxTestUtil.initJavaFx();
    }

    @Test
    void loadDashboardData_filtersToHourlyRates_andUpdatesObservableList() {
        List<UnitRate> rates = List.of(
                new UnitRate(10.0, ZonedDateTime.of(2026, 3, 4, 10, 0, 0, 0, ZoneOffset.UTC)),
                new UnitRate(11.0, ZonedDateTime.of(2026, 3, 4, 10, 30, 0, 0, ZoneOffset.UTC)),
                new UnitRate(12.0, ZonedDateTime.of(2026, 3, 4, 11, 0, 0, 0, ZoneOffset.UTC))
        );
        when(repository.fetchNext12Hours()).thenReturn(rates);

        AdvancedDashboardViewModel viewModel = new AdvancedDashboardViewModel(repository);
        viewModel.loadDashboardData();
        JavaFxTestUtil.waitForFxEvents();

        assertEquals(2, viewModel.getHourlyForecast().size());
        assertEquals(10.0, viewModel.getHourlyForecast().get(0).valueIncVat());
        assertEquals(12.0, viewModel.getHourlyForecast().get(1).valueIncVat());
    }

    @Test
    void loadDashboardData_whenRepositoryReturnsEmptyList_forecastIsEmpty() {
        when(repository.fetchNext12Hours()).thenReturn(List.of());

        AdvancedDashboardViewModel viewModel = new AdvancedDashboardViewModel(repository);
        viewModel.loadDashboardData();
        JavaFxTestUtil.waitForFxEvents();

        assertEquals(0, viewModel.getHourlyForecast().size());
    }

    @Test
    void loadDashboardData_whenNoFullHourRates_listIsEmpty() {
        List<UnitRate> rates = List.of(
                new UnitRate(10.0, ZonedDateTime.of(2026,3,4,10,30,0,0,ZoneOffset.UTC)),
                new UnitRate(11.0, ZonedDateTime.of(2026,3,4,11,30,0,0,ZoneOffset.UTC))
        );

        when(repository.fetchNext12Hours()).thenReturn(rates);

        AdvancedDashboardViewModel viewModel = new AdvancedDashboardViewModel(repository);
        viewModel.loadDashboardData();
        JavaFxTestUtil.waitForFxEvents();

        assertEquals(0, viewModel.getHourlyForecast().size());
    }

    @Test
    void loadDashboardData_replacesExistingForecast() {
        List<UnitRate> first = List.of(
                new UnitRate(10.0, ZonedDateTime.of(2026,3,4,10,0,0,0,ZoneOffset.UTC))
        );

        List<UnitRate> second = List.of(
                new UnitRate(20.0, ZonedDateTime.of(2026,3,4,11,0,0,0,ZoneOffset.UTC))
        );

        when(repository.fetchNext12Hours())
                .thenReturn(first)
                .thenReturn(second);

        AdvancedDashboardViewModel viewModel = new AdvancedDashboardViewModel(repository);

        viewModel.loadDashboardData();
        JavaFxTestUtil.waitForFxEvents();

        viewModel.loadDashboardData();
        JavaFxTestUtil.waitForFxEvents();

        assertEquals(1, viewModel.getHourlyForecast().size());
        assertEquals(20.0, viewModel.getHourlyForecast().getFirst().valueIncVat());
    }
}
