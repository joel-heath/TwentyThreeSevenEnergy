package uk.ac.soton.comp2300.group42.energyclient.data.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UnitRateResponse(
        @JsonProperty("value_inc_vat")
        double valueIncVat,

        @JsonProperty("valid_from")
        String validFrom
) {}