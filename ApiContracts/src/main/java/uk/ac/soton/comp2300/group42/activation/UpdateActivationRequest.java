package uk.ac.soton.comp2300.group42.activation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record UpdateActivationRequest(
        @NotNull(message = "Appliance ID is required")
        Long applianceId,

        @NotNull(message = "House ID is required")
        Long houseId,

        @NotNull(message = "Activation type is required")
        ActivationType type,

        @NotNull(message = "Activation time is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime activationTime,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate activationDate,

        Boolean recursMonday,
        Boolean recursTuesday,
        Boolean recursWednesday,
        Boolean recursThursday,
        Boolean recursFriday,
        Boolean recursSaturday,
        Boolean recursSunday
) {
    @JsonIgnore
    @AssertTrue(message = "Activation date is required when type is NON_RECURRING")
    public boolean isDateNonNullWhenNonRecurring() {
        return type != ActivationType.NON_RECURRING || activationDate != null;
    }

    @JsonIgnore
    @AssertTrue(message = "Activation date must be null when type is RECURRING")
    public boolean isDateNullWhenRecurring() {
        return type != ActivationType.RECURRING || activationDate == null;
    }

    @JsonIgnore
    @AssertTrue(message = "All recurrence flags must be null when type is NON_RECURRING")
    public boolean isRecurrenceDaysNullWhenNonRecurring() {
        return type != ActivationType.NON_RECURRING || (recursMonday == null && recursTuesday == null && recursWednesday == null && recursThursday == null && recursFriday == null && recursSaturday == null && recursSunday == null);
    }

    @JsonIgnore
    @AssertTrue(message = "All recurrence flags must not be null when type is RECURRING")
    public boolean isRecurrenceDaysNonNullWhenRecurring() {
        return type != ActivationType.RECURRING || (recursMonday != null && recursTuesday != null && recursWednesday != null && recursThursday != null && recursFriday != null && recursSaturday != null && recursSunday != null);
    }

    @JsonIgnore
    @AssertTrue(message = "At least one recurrence day must be true when type is RECURRING")
    public boolean isAtLeastOneRecurrenceDayWhenRecurring() {
        return type != ActivationType.RECURRING || recurs(recursMonday) || recurs(recursTuesday) || recurs(recursWednesday) || recurs(recursThursday) || recurs(recursFriday) || recurs(recursSaturday) || recurs(recursSunday);
    }

    private boolean recurs(Boolean day) {
        return Objects.equals(day, true);
    }
}