package uk.ac.soton.comp2300.group42.preferences;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Theme {
    
    LIGHT("light", "Light"),
    DARK("dark", "Dark"),
    LIGHT_CONTRAST("light_high_contrast", "Light (High Contrast)"),
    DARK_CONTRAST("dark_high_contrast", "Dark (High Contrast)");

    private final String id;
    private final String name;

    Theme(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonValue
    public String getId() { return id; }

    public String getName() { return name; }

    private static final Map<String, Theme> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Theme::getId, m -> m));

    @JsonCreator
    public static Theme fromId(String id) {
        if (id == null)
            throw new IllegalArgumentException("Theme id cannot be null");

        Theme mode = BY_ID.get(id);

        if (mode == null)
            throw new IllegalArgumentException("Unknown theme id: " + id);

        return mode;
    }
}
