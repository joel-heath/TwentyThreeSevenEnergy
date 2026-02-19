package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import com.google.gson.annotations.SerializedName;

public record UnitRate(
        @SerializedName("value_inc_vat") double valueIncVat,
        @SerializedName("valid_from") String validFrom
) {}