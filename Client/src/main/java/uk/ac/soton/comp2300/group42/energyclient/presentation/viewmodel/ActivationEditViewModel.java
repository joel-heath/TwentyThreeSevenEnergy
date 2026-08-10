package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ActivationEditViewModel {

    private final ActivationService activationService;
    private final ObservableList<ObservableAppliance> applianceList;

    private ObservableActivation currentActivation;

    private final ObjectProperty<ObservableAppliance> selectedAppliance = new SimpleObjectProperty<>();
    private final IntegerProperty hour = new SimpleIntegerProperty(0);
    private final IntegerProperty minute = new SimpleIntegerProperty(0);
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(LocalDate.now());

    private final BooleanProperty recursMonday = new SimpleBooleanProperty(false);
    private final BooleanProperty recursTuesday = new SimpleBooleanProperty(false);
    private final BooleanProperty recursWednesday = new SimpleBooleanProperty(false);
    private final BooleanProperty recursThursday = new SimpleBooleanProperty(false);
    private final BooleanProperty recursFriday = new SimpleBooleanProperty(false);
    private final BooleanProperty recursSaturday = new SimpleBooleanProperty(false);
    private final BooleanProperty recursSunday = new SimpleBooleanProperty(false);
    private final BooleanProperty isRecurring = new SimpleBooleanProperty(false);

    private final StringProperty responseMessage = new SimpleStringProperty("");
    private final BooleanProperty hasResponseError = new SimpleBooleanProperty(false);

    @Inject public ActivationEditViewModel(ActivationService activationService, ApplianceStore applianceStore) {
        this.activationService = activationService;
        this.applianceList = applianceStore.getAll();
    }

    public void loadActivation(ObservableActivation activation) {
        this.currentActivation = activation;
        
        selectedAppliance.set(activation.getAppliance());
        hour.set(activation.getActivationTime().getHour());
        minute.set(activation.getActivationTime().getMinute());
        
        isRecurring.set(activation.getActivationDate() == null);
        date.set(activation.getActivationDate() != null ? activation.getActivationDate() : LocalDate.now());
        
        recursMonday.set(Boolean.TRUE.equals(activation.isRecursMonday()));
        recursTuesday.set(Boolean.TRUE.equals(activation.isRecursTuesday()));
        recursWednesday.set(Boolean.TRUE.equals(activation.isRecursWednesday()));
        recursThursday.set(Boolean.TRUE.equals(activation.isRecursThursday()));
        recursFriday.set(Boolean.TRUE.equals(activation.isRecursFriday()));
        recursSaturday.set(Boolean.TRUE.equals(activation.isRecursSaturday()));
        recursSunday.set(Boolean.TRUE.equals(activation.isRecursSunday()));
        
        responseMessage.set("");
    }

    public boolean saveChanges() {
        if (guard(selectedAppliance.get() == null, "Failed to schedule, no appliance selected") ||
            guard(isRecurring.get() && !hasRecurrenceDaysSelected(), "Failed to schedule, no recurrence days selected") ||
            guard(!isRecurring.get() && date.get().atTime(hour.get(), minute.get()).isBefore(LocalDateTime.now()), "Failed to schedule, selected date is in the past"))
            return false;

        currentActivation.setAppliance(selectedAppliance.get());
        currentActivation.setActivationTime(LocalTime.of(hour.get(), minute.get()));

        if (isRecurring.get()) {
            currentActivation.setActivationDate(null);
            currentActivation.setRecursMonday(recursMonday.get());
            currentActivation.setRecursTuesday(recursTuesday.get());
            currentActivation.setRecursWednesday(recursWednesday.get());
            currentActivation.setRecursThursday(recursThursday.get());
            currentActivation.setRecursFriday(recursFriday.get());
            currentActivation.setRecursSaturday(recursSaturday.get());
            currentActivation.setRecursSunday(recursSunday.get());
        } else {
            currentActivation.setActivationDate(date.get());
            currentActivation.setRecursMonday(null);
            currentActivation.setRecursTuesday(null);
            currentActivation.setRecursWednesday(null);
            currentActivation.setRecursThursday(null);
            currentActivation.setRecursFriday(null);
            currentActivation.setRecursSaturday(null);
            currentActivation.setRecursSunday(null);
        }

        try {
            activationService.save(currentActivation);
            return true;
        } catch (ApiException e) {
            setErrorResponse("Failed to update: " + e.getMessage());
            return false;
        }
    }

    public void deleteActivation() {
        if (currentActivation != null) {
            activationService.delete(currentActivation);
        }
    }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            setErrorResponse(errorMessage);

        return condition;
    }

    private boolean hasRecurrenceDaysSelected() {
        return recursMonday.get() || recursTuesday.get() || recursWednesday.get() || recursThursday.get() || recursFriday.get() || recursSaturday.get() || recursSunday.get();
    }

    private void setErrorResponse(String message) {
        responseMessage.set(message);
        hasResponseError.set(true);
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

    public StringProperty responseMessageProperty() { return responseMessage; }
    public BooleanProperty hasResponseErrorProperty() { return hasResponseError; }
}
