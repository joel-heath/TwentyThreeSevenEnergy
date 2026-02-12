package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

public class ScheduleViewModel {

    private final Repository repository;
    private final ObservableList<ApplianceModel> applianceList;
    private final ObjectProperty<ApplianceModel> selectedAppliance;
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

    public ScheduleViewModel(Repository repository) {
        this.repository = repository;
        this.applianceList = repository.getAppliances();
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

        CompletableFuture.runAsync(repository::fetchAllData);
    }

    public ObservableList<ApplianceModel> getApplianceList() { return applianceList; }
    public ApplianceModel getSelectedAppliance() { return selectedAppliance.get(); }
    public ObjectProperty<ApplianceModel> selectedApplianceProperty() { return selectedAppliance; }

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
        ActivationDTO dto = isRecurring.get()
                ? new ActivationDTO(
                        selectedAppliance.get().getId(),
                        LocalTime.of(hour.get(), minute.get()),
                        recursMonday.get(),
                        recursTuesday.get(),
                        recursWednesday.get(),
                        recursThursday.get(),
                        recursFriday.get(),
                        recursSaturday.get(),
                        recursSunday.get())
                : new ActivationDTO(
                        selectedAppliance.get().getId(),
                        LocalTime.of(hour.get(), minute.get()),
                        date.get());

        return repository.createActivation(dto);
    }
}