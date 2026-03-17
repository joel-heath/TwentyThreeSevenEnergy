package uk.ac.soton.comp2300.group42.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EnergyCategory {

    ELECTRICITY("electricity", "Electricity"),
    GAS("gas", "Gas"),
    WATER("water", "Water"),
    OTHER("other", "Other");


    private final String id;
    private final String name;

    EnergyCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonValue
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, EnergyCategory> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(EnergyCategory::getId, m -> m));


    @JsonCreator
    public static EnergyCategory fromId(String id) {
        if (id == null)
            throw new IllegalArgumentException("Energy category id cannot be null");

        EnergyCategory mode = BY_ID.get(id);

        if (mode == null)
            throw new IllegalArgumentException("Unknown energy category id: " + id);

        return mode;
    }
}
