package uk.ac.soton.comp2300.group42.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Role {
    OWNER("owner", "Owner"),
    RESIDENT("resident", "Resident"),
    GUEST("guest", "Guest");

    private final String id;
    private final String name;

    Role(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonValue
    public String getId() { return id; }

    public String getName() { return name; }

    private static final Map<String, Role> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Role::getId, m -> m));

    @JsonCreator
    public static Role fromId(String id) {
        Role mode = BY_ID.get(id);
        if (mode == null)
            throw new IllegalArgumentException("Unknown role id: " + id);
        return mode;
    }
}
