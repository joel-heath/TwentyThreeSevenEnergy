package uk.ac.soton.comp2300.group42.energyclient.data.api;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Theme {
    LIGHT("light"),
    DARK("dark"),
    HIGH_CONTRAST("high_contrast");

    private final String id;

    Theme(String id) { this.id = id; }

    public String id() { return id; }

    private static final Map<String, Theme> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Theme::id, m -> m));

    public static Theme fromId(String id) {
        Theme mode = BY_ID.get(id);
        if (mode == null)
            throw new IllegalArgumentException("Unknown mode id: " + id);
        return mode;
    }
}

