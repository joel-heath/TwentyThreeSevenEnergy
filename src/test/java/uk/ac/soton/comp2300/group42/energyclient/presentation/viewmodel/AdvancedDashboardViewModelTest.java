package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvancedDashboardViewModelTest {

    @Mock private EnergyPriceRepository energyPriceRepository;

    private AdvancedDashboardViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new AdvancedDashboardViewModel(energyPriceRepository, Runnable::run);
    }

    @Test
    void loadDashboardData_mapsStatusesAndFiltersNonHourlyRates() {
        ZonedDateTime base = ZonedDateTime.of(LocalDateTime.of(2026, 1, 1, 10, 0), ZoneId.of("UTC"));
        when(energyPriceRepository.fetchNext12Hours()).thenReturn(List.of(
                new UnitRate(10.0, base),
                new UnitRate(11.0, base.plusMinutes(30)),
                new UnitRate(25.0, base.plusHours(1)),
                new UnitRate(35.0, base.plusHours(2))
        ));

        viewModel.loadDashboardData();

        await(() -> viewModel.getHourlyForecast().size() == 3);
        assertEquals("10:00", viewModel.getHourlyForecast().get(0).timeText());
        assertEquals("status-cheap", viewModel.getHourlyForecast().get(0).statusStyleClass());
        assertEquals("11:00", viewModel.getHourlyForecast().get(1).timeText());
        assertEquals("status-average", viewModel.getHourlyForecast().get(1).statusStyleClass());
        assertEquals("12:00", viewModel.getHourlyForecast().get(2).timeText());
        assertEquals("status-expensive", viewModel.getHourlyForecast().get(2).statusStyleClass());
    }

    @Test
    void loadDashboardData_whenRepositoryFails_keepsForecastEmpty() {
        when(energyPriceRepository.fetchNext12Hours()).thenThrow(new RuntimeException("down"));

        viewModel.loadDashboardData();

        verify(energyPriceRepository, timeout(1000)).fetchNext12Hours();
        assertTrue(viewModel.getHourlyForecast().isEmpty());
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

