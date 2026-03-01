package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleViewModel {

    private final ActivationService activationService;
    private final ObservablePreferences preferences;
    private final ObservableList<ObservableAppliance> applianceList;
    private final ObjectProperty<ObservableAppliance> selectedAppliance;
    private final IntegerProperty hour;
    private final IntegerProperty minute;
    private final ObjectProperty<LocalDate> date;
    private final BooleanProperty recursMonday;
    private final BooleanProperty recursTuesday;
    private final BooleanProperty recursWednesday;
    private final BooleanProperty recursThursday;
    private final BooleanProperty recursFriday;
    private final BooleanProperty recursSaturday;
    private final BooleanProperty recursSunday;
    private final BooleanProperty isRecurring;

    @Inject public ScheduleViewModel(ActivationService activationService, ApplianceStore applianceStore, ObservablePreferences preferences) {
        this.activationService = activationService;
        this.applianceList = applianceStore.getAll();
        this.preferences = preferences;
        selectedAppliance = new SimpleObjectProperty<>();
        hour = new SimpleIntegerProperty(LocalDateTime.now().getHour());
        minute = new SimpleIntegerProperty(LocalDateTime.now().getMinute());
        date = new SimpleObjectProperty<>(LocalDate.now());
        recursMonday = new SimpleBooleanProperty(false);
        recursTuesday = new SimpleBooleanProperty(false);
        recursWednesday = new SimpleBooleanProperty(false);
        recursThursday = new SimpleBooleanProperty(false);
        recursFriday = new SimpleBooleanProperty(false);
        recursSaturday = new SimpleBooleanProperty(false);
        recursSunday = new SimpleBooleanProperty(false);
        isRecurring = new SimpleBooleanProperty(false);

        // CompletableFuture.runAsync(fetchAllData);
    }

    public ObservableList<ObservableAppliance> getApplianceList() { return applianceList; }
    public ObjectProperty<ObservableAppliance> selectedApplianceProperty() { return selectedAppliance; }
    public IntegerProperty hourProperty() { return hour; }
    public IntegerProperty minuteProperty() { return minute; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public BooleanProperty recursMondayProperty() { return recursMonday; }
    public BooleanProperty recursTuesdayProperty() { return recursTuesday; }
    public BooleanProperty recursWednesdayProperty() { return recursWednesday; }
    public BooleanProperty recursThursdayProperty() { return recursThursday; }
    public BooleanProperty recursFridayProperty() { return recursFriday; }
    public BooleanProperty recursSaturdayProperty() { return recursSaturday; }
    public BooleanProperty recursSundayProperty() { return recursSunday; }
    public BooleanProperty isRecurringProperty() { return isRecurring; }

    public LocalDateTime scheduleActivation() {
        Activation pojo = isRecurring.get()
                ? new Activation(
                        selectedAppliance.get().getId(),
                        preferences.getActiveHouse().getId(),
                        LocalTime.of(hour.get(), minute.get()),
                        recursMonday.get(),
                        recursTuesday.get(),
                        recursWednesday.get(),
                        recursThursday.get(),
                        recursFriday.get(),
                        recursSaturday.get(),
                        recursSunday.get())
                : new Activation(
                        selectedAppliance.get().getId(),
                        preferences.getActiveHouse().getId(),
                        LocalTime.of(hour.get(), minute.get()),
                        date.get());

        return activationService.create(pojo);
    }
}