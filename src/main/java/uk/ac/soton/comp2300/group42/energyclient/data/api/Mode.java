package uk.ac.soton.comp2300.group42.energyclient.data.api;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Mode {
    SIMPLE("simple"),
    ADVANCED("advanced");

    private final String id;

    Mode(String id) { this.id = id; }

    public String id() { return id; }

    private static final Map<String, Mode> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Mode::id, m -> m));

    public static Mode fromId(String id) {
        Mode mode = BY_ID.get(id);
        if (mode == null)
            throw new IllegalArgumentException("Unknown mode id: " + id);
        return mode;
    }
}
