package uk.ac.soton.comp2300.group42.activation;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateActivationRequest(
        Long applianceId,
        ActivationType type,

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
) {}