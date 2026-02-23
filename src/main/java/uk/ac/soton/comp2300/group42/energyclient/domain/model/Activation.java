package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import uk.ac.soton.comp2300.group42.activation.ActivationType;

import java.time.LocalDate;
import java.time.LocalTime;

public record Activation(
        Long id,
        Appliance appliance,
        ActivationType type,
        LocalTime activationTime,
        LocalDate activationDate,
        Boolean recursMonday,
        Boolean recursTuesday,
        Boolean recursWednesday,
        Boolean recursThursday,
        Boolean recursFriday,
        Boolean recursSaturday,
        Boolean recursSunday
) {}
