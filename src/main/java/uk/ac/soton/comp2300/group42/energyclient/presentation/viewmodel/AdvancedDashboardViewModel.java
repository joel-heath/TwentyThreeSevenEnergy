package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.PriceStatus;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AdvancedDashboardViewModel {

    public record StatusCardState(String timeText, String emoji, String statusStyleClass) {}

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final Executor uiExecutor;
    private final EnergyPriceRepository repository;
    private final ObservableList<StatusCardState> hourlyForecast;

    @Inject
    public AdvancedDashboardViewModel(EnergyPriceRepository repository, @UIExecutor Executor uiExecutor) {
        this.repository = repository;
        this.uiExecutor = uiExecutor;
        this.hourlyForecast = FXCollections.observableArrayList();
    }

    public void loadDashboardData() {
        CompletableFuture.supplyAsync(repository::fetchNext12Hours)
                .thenAcceptAsync(rates -> {
                    List<StatusCardState> mappedStates = rates.stream()
                            .filter(rate -> rate.validFrom().getMinute() == 0)
                            .map(this::mapToState)
                            .toList();

                    hourlyForecast.setAll(mappedStates);
                }, uiExecutor)
                .exceptionallyAsync(e -> {
                    System.err.println("Failed to load dashboard data: " + e.getMessage());
                    return null;
                }, uiExecutor);
    }

    private StatusCardState mapToState(UnitRate rate) {
        String timeText = rate.validFrom().format(TIME_FORMATTER);
        PriceStatus status = rate.getPriceStatus();

        return switch (status) {
            case CHEAP -> new StatusCardState(timeText, "\uD83D\uDE0A", "status-cheap");
            case AVERAGE -> new StatusCardState(timeText, "\uD83D\uDE10", "status-average");
            case EXPENSIVE -> new StatusCardState(timeText, "\uD83D\uDE41", "status-expensive");
        };
    }

    public ObservableList<StatusCardState> getHourlyForecast() {
        return hourlyForecast;
    }
}
