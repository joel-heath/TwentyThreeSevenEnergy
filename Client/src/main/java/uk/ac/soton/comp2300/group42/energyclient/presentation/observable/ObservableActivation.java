package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ModelUtils.updateIfChanged;

public class ObservableActivation {

    private final Long id;
    private final ObjectProperty<ObservableAppliance> appliance;
    private final ObjectProperty<LocalTime> activationTime;
    private final ObjectProperty<LocalDate> activationDate;
    private final ObjectProperty<ActivationType> type;
    private final ObjectProperty<Boolean> recursMonday;
    private final ObjectProperty<Boolean> recursTuesday;
    private final ObjectProperty<Boolean> recursWednesday;
    private final ObjectProperty<Boolean> recursThursday;
    private final ObjectProperty<Boolean> recursFriday;
    private final ObjectProperty<Boolean> recursSaturday;
    private final ObjectProperty<Boolean> recursSunday;
    private final BooleanProperty updateTrigger;

    public ObservableActivation(Activation entity, ObservableAppliance appliance) {
        this.id = entity.id();
        this.appliance = new SimpleObjectProperty<>(appliance);
        this.activationTime = new SimpleObjectProperty<>(entity.activationTime());
        this.activationDate = new SimpleObjectProperty<>(entity.activationDate());
        this.type = new SimpleObjectProperty<>(entity.type());
        this.recursMonday = new SimpleObjectProperty<>(entity.recursMonday());
        this.recursTuesday = new SimpleObjectProperty<>(entity.recursTuesday());
        this.recursWednesday = new SimpleObjectProperty<>(entity.recursWednesday());
        this.recursThursday = new SimpleObjectProperty<>(entity.recursThursday());
        this.recursFriday = new SimpleObjectProperty<>(entity.recursFriday());
        this.recursSaturday = new SimpleObjectProperty<>(entity.recursSaturday());
        this.recursSunday = new SimpleObjectProperty<>(entity.recursSunday());
        this.updateTrigger = new SimpleBooleanProperty(false);
    }

    public Activation commit() {
        return new Activation(
                getId(),
                getAppliance().getId(),
                getAppliance().getHouse().getId(),
                type.get(),
                getActivationTime(),
                getActivationDate(),
                isRecursMonday(),
                isRecursTuesday(),
                isRecursWednesday(),
                isRecursThursday(),
                isRecursFriday(),
                isRecursSaturday(),
                isRecursSunday()
        );
    }

    public void updateFrom(Activation entity, ObservableAppliance appliance) {
        updateIfChanged(getActivationTime(), entity.activationTime(), this::setActivationTime);
        updateIfChanged(getActivationDate(), entity.activationDate(), this::setActivationDate);
        updateIfChanged(getActivationType(), entity.type(), this::setActivationType);
        updateIfChanged(isRecursMonday(), entity.recursMonday(), this::setRecursMonday);
        updateIfChanged(isRecursTuesday(), entity.recursTuesday(), this::setRecursTuesday);
        updateIfChanged(isRecursWednesday(), entity.recursWednesday(), this::setRecursWednesday);
        updateIfChanged(isRecursThursday(), entity.recursThursday(), this::setRecursThursday);
        updateIfChanged(isRecursFriday(), entity.recursFriday(), this::setRecursFriday);
        updateIfChanged(isRecursSaturday(), entity.recursSaturday(), this::setRecursSaturday);
        updateIfChanged(isRecursSunday(), entity.recursSunday(), this::setRecursSunday);
        updateIfChanged(getAppliance(), appliance, this::setAppliance);
    }

    public Long getId() { return id; }

    public ObservableAppliance getAppliance() { return appliance.get(); }
    public void setAppliance(ObservableAppliance appliance) { this.appliance.set(appliance); }
    public ObjectProperty<ObservableAppliance> applianceProperty() { return appliance; }

    public LocalTime getActivationTime() { return activationTime.get(); }
    public void setActivationTime(LocalTime activationTime) { this.activationTime.set(activationTime); }
    public ObjectProperty<LocalTime> activationTimeProperty() { return activationTime; }

    public LocalDate getActivationDate() { return activationDate.get(); }
    public void setActivationDate(LocalDate activationDate) { this.activationDate.set(activationDate); }
    public ObjectProperty<LocalDate> activationDateProperty() { return activationDate; }

    public ActivationType getActivationType() { return type.get(); }
    public void setActivationType(ActivationType type) { this.type.set(type); }
    public ObjectProperty<ActivationType> activationTypeProperty() { return type; }

    public Boolean isRecursMonday() { return recursMonday.get(); }
    public Boolean isRecursTuesday() { return recursTuesday.get(); }
    public Boolean isRecursWednesday() { return recursWednesday.get(); }
    public Boolean isRecursThursday() { return recursThursday.get(); }
    public Boolean isRecursFriday() { return recursFriday.get(); }
    public Boolean isRecursSaturday() { return recursSaturday.get(); }
    public Boolean isRecursSunday() { return recursSunday.get(); }

    public void setRecursMonday(Boolean recursMonday) { this.recursMonday.set(recursMonday); }
    public void setRecursTuesday(Boolean recursTuesday) { this.recursTuesday.set(recursTuesday); }
    public void setRecursWednesday(Boolean recursWednesday) { this.recursWednesday.set(recursWednesday); }
    public void setRecursThursday(Boolean recursThursday) { this.recursThursday.set(recursThursday); }
    public void setRecursFriday(Boolean recursFriday) { this.recursFriday.set(recursFriday); }
    public void setRecursSaturday(Boolean recursSaturday) { this.recursSaturday.set(recursSaturday); }
    public void setRecursSunday(Boolean recursSunday) { this.recursSunday.set(recursSunday); }

    public ObjectProperty<Boolean> recursMondayProperty() { return recursMonday; }
    public ObjectProperty<Boolean> recursTuesdayProperty() { return recursTuesday; }
    public ObjectProperty<Boolean> recursWednesdayProperty() { return recursWednesday; }
    public ObjectProperty<Boolean> recursThursdayProperty() { return recursThursday; }
    public ObjectProperty<Boolean> recursFridayProperty() { return recursFriday; }
    public ObjectProperty<Boolean> recursSaturdayProperty() { return recursSaturday; }
    public ObjectProperty<Boolean> recursSundayProperty() { return recursSunday; }

    public boolean recursOnDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case DayOfWeek.MONDAY -> Boolean.TRUE.equals(isRecursMonday());
            case DayOfWeek.TUESDAY -> Boolean.TRUE.equals(isRecursTuesday());
            case DayOfWeek.WEDNESDAY -> Boolean.TRUE.equals(isRecursWednesday());
            case DayOfWeek.THURSDAY -> Boolean.TRUE.equals(isRecursThursday());
            case DayOfWeek.FRIDAY -> Boolean.TRUE.equals(isRecursFriday());
            case DayOfWeek.SATURDAY -> Boolean.TRUE.equals(isRecursSaturday());
            case DayOfWeek.SUNDAY -> Boolean.TRUE.equals(isRecursSunday());
        };
    }

    public LocalDateTime getNextActivationDateTime() {
        return getNextActivationDateTime(LocalDateTime.now());
    }

    public LocalDateTime getNextActivationDateTime(LocalDateTime now) {
        if (getActivationType() == ActivationType.NON_RECURRING)
            return LocalDateTime.of(getActivationDate(), getActivationTime());

        var today = now.getDayOfWeek();
        if (recursOnDay(today)) {
            var candidateTime = LocalDateTime.of(now.toLocalDate(), getActivationTime());
            if (candidateTime.isAfter(now))
                return candidateTime;
        }

        var t = today.getValue();
        for (int i = 1; i <= 7; i++) {
            int day = (t + i - 1) % 7 + 1;
            if (recursOnDay(DayOfWeek.of(day))) {
                return LocalDateTime.of(now.toLocalDate().plusDays(i), getActivationTime());
            }
        }

        throw new IllegalStateException("Recurring activation must have at least one day selected");
    }

    public BooleanProperty updateTriggerProperty() { return updateTrigger; }
    public void triggerUpdate() { updateTrigger.set(!updateTrigger.get()); }
}
