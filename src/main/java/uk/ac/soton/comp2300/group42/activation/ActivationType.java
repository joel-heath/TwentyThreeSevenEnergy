package uk.ac.soton.comp2300.group42.activation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ActivationType {
    RECURRING("recurring", "Recurring"),
    NON_RECURRING("non_recurring", "One-off");

    private final String id;
    private final String name;

    ActivationType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonValue
    public String getId() { return id; }

    public String getName() { return name; }

    private static final Map<String, ActivationType> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(ActivationType::getId, m -> m));

    @JsonCreator
    public static ActivationType fromId(String id) {
        ActivationType mode = BY_ID.get(id);
        if (mode == null)
            throw new IllegalArgumentException("Unknown activation type id: " + id);
        return mode;
    }
}
