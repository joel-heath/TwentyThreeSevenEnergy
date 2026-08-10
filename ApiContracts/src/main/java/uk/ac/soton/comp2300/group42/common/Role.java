package uk.ac.soton.comp2300.group42.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Role {
    OWNER("owner", "Owner", 100),
    RESIDENT("resident", "Resident", 50),
    GUEST("guest", "Guest", 10);

    private final String id;
    private final String name;
    private final int level;

    Role(String id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    @JsonValue
    public String getId() { return id; }

    public String getName() { return name; }

    public int getLevel() { return level; }

    private static final Map<String, Role> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Role::getId, m -> m));

    @JsonCreator
    public static Role fromId(String id) {
        if (id == null)
            throw new IllegalArgumentException("Role id cannot be null");

        Role mode = BY_ID.get(id);

        if (mode == null)
            throw new IllegalArgumentException("Unknown role id: " + id);

        return mode;
    }
}
