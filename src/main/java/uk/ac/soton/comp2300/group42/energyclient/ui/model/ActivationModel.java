package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelUtils.updateIfChanged;

public class ActivationModel {
    private final ObjectProperty<ApplianceModel> appliance;
    private final ObjectProperty<LocalTime> activationTime;
    private final ObjectProperty<LocalDate> activationDate;
    private final BooleanProperty recursMonday;
    private final BooleanProperty recursTuesday;
    private final BooleanProperty recursWednesday;
    private final BooleanProperty recursThursday;
    private final BooleanProperty recursFriday;
    private final BooleanProperty recursSaturday;
    private final BooleanProperty recursSunday;
    private final ActivationDTO dto;

    public ActivationModel(ActivationDTO dto, ApplianceModel appliance) {
        this.dto = dto;
        this.appliance = new SimpleObjectProperty<>(appliance);
        this.activationTime = new SimpleObjectProperty<>(dto.getActivationTime());
        this.activationDate = new SimpleObjectProperty<>(dto.getActivationDate());
        this.recursMonday = new SimpleBooleanProperty(dto.isRecursMonday());
        this.recursTuesday = new SimpleBooleanProperty(dto.isRecursTuesday());
        this.recursWednesday = new SimpleBooleanProperty(dto.isRecursWednesday());
        this.recursThursday = new SimpleBooleanProperty(dto.isRecursThursday());
        this.recursFriday = new SimpleBooleanProperty(dto.isRecursFriday());
        this.recursSaturday = new SimpleBooleanProperty(dto.isRecursSaturday());
        this.recursSunday = new SimpleBooleanProperty(dto.isRecursSunday());
    }

    public ActivationDTO commit() {
        dto.setApplianceId(appliance.get().getId());
        dto.setActivationTime(activationTime.get());
        dto.setActivationDate(activationDate.get());
        dto.setRecursMonday(recursMonday.get());
        dto.setRecursTuesday(recursTuesday.get());
        dto.setRecursWednesday(recursWednesday.get());
        dto.setRecursThursday(recursThursday.get());
        dto.setRecursFriday(recursFriday.get());
        dto.setRecursSaturday(recursSaturday.get());
        dto.setRecursSunday(recursSunday.get());
        return dto;
    }

    public void updateFrom(ActivationDTO dto, ApplianceModel appliance) {
        updateIfChanged(getActivationTime(), dto.getActivationTime(), this::setActivationTime);
        updateIfChanged(getActivationDate(), dto.getActivationDate(), this::setActivationDate);
        updateIfChanged(isRecursMonday(), dto.isRecursMonday(), this::setRecursMonday);
        updateIfChanged(isRecursTuesday(), dto.isRecursTuesday(), this::setRecursTuesday);
        updateIfChanged(isRecursWednesday(), dto.isRecursWednesday(), this::setRecursWednesday);
        updateIfChanged(isRecursThursday(), dto.isRecursThursday(), this::setRecursThursday);
        updateIfChanged(isRecursFriday(), dto.isRecursFriday(), this::setRecursFriday);
        updateIfChanged(isRecursSaturday(), dto.isRecursSaturday(), this::setRecursSaturday);
        updateIfChanged(isRecursSunday(), dto.isRecursSunday(), this::setRecursSunday);

        if (!getAppliance().getId().equals(dto.getApplianceId()))
            setAppliance(appliance);
    }

    public Long getId() { return dto.getId(); }

    public ApplianceModel getAppliance() { return appliance.get(); }
    public void setAppliance(ApplianceModel appliance) { this.appliance.set(appliance); }
    public ObjectProperty<ApplianceModel> applianceProperty() { return appliance; }

    public LocalTime getActivationTime() { return activationTime.get(); }
    public void setActivationTime(LocalTime activationTime) { this.activationTime.set(activationTime); }
    public ObjectProperty<LocalTime> activationTimeProperty() { return activationTime; }

    public LocalDate getActivationDate() { return activationDate.get(); }
    public void setActivationDate(LocalDate activationDate) { this.activationDate.set(activationDate); }
    public ObjectProperty<LocalDate> activationDateProperty() { return activationDate; }

    public Boolean isRecursMonday() { return recursMonday.get(); }
    public Boolean isRecursTuesday() { return recursTuesday.get(); }
    public Boolean isRecursWednesday() { return recursWednesday.get(); }
    public Boolean isRecursThursday() { return recursThursday.get(); }
    public Boolean isRecursFriday() { return recursFriday.get(); }
    public Boolean isRecursSaturday() { return recursSaturday.get(); }
    public Boolean isRecursSunday() { return recursSunday.get(); }

    public void setRecursMonday(boolean recursMonday) { this.recursMonday.set(recursMonday); }
    public void setRecursTuesday(boolean recursTuesday) { this.recursTuesday.set(recursTuesday); }
    public void setRecursWednesday(boolean recursWednesday) { this.recursWednesday.set(recursWednesday); }
    public void setRecursThursday(boolean recursThursday) { this.recursThursday.set(recursThursday); }
    public void setRecursFriday(boolean recursFriday) { this.recursFriday.set(recursFriday); }
    public void setRecursSaturday(boolean recursSaturday) { this.recursSaturday.set(recursSaturday); }
    public void setRecursSunday(boolean recursSunday) { this.recursSunday.set(recursSunday); }

    public BooleanProperty recursMondayProperty() { return recursMonday; }
    public BooleanProperty recursTuesdayProperty() { return recursTuesday; }
    public BooleanProperty recursWednesdayProperty() { return recursWednesday; }
    public BooleanProperty recursThursdayProperty() { return recursThursday; }
    public BooleanProperty recursFridayProperty() { return recursFriday; }
    public BooleanProperty recursSaturdayProperty() { return recursSaturday; }
    public BooleanProperty recursSundayProperty() { return recursSunday; }

    public boolean isRecurring() { return getActivationDate() == null; }

    public boolean recursOnDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case DayOfWeek.MONDAY -> isRecursMonday();
            case DayOfWeek.TUESDAY -> isRecursTuesday();
            case DayOfWeek.WEDNESDAY -> isRecursWednesday();
            case DayOfWeek.THURSDAY -> isRecursThursday();
            case DayOfWeek.FRIDAY -> isRecursFriday();
            case DayOfWeek.SATURDAY -> isRecursSaturday();
            case DayOfWeek.SUNDAY -> isRecursSunday();
        };
    }

    public LocalDateTime getNextActivationDateTime() {
        return getNextActivationDateTime(LocalDateTime.now());
    }

    public LocalDateTime getNextActivationDateTime(LocalDateTime now) {
        if (!isRecurring())
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
}
