package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import uk.ac.soton.comp2300.group42.activation.ActivationType;

import java.time.LocalDate;
import java.time.LocalTime;

public record Activation(
        Long id,
        Long applianceId,
        Long houseId,
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
) {
    public Activation(Long applianceId, Long houseId, LocalTime activationTime, LocalDate activationDate) {
        this(null, applianceId, houseId, ActivationType.NON_RECURRING, activationTime, activationDate,
             null, null, null, null, null, null, null);
    }

    public Activation(Long applianceId, Long houseId, LocalTime activationTime,
                      Boolean recursMonday, Boolean recursTuesday, Boolean recursWednesday, Boolean recursThursday,
                      Boolean recursFriday, Boolean recursSaturday, Boolean recursSunday) {
        this(null, applianceId, houseId, ActivationType.RECURRING, activationTime, null,
             recursMonday, recursTuesday, recursWednesday, recursThursday,
             recursFriday, recursSaturday, recursSunday);
    }
}
