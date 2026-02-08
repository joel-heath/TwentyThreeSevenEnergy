package uk.ac.soton.comp2300.group42.energyclient.data.api;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Mode {
    SIMPLE("simple", "Simple (Recommended)"),
    ADVANCED("advanced", "Advanced"),;

    private final String id;
    private final String name;

    Mode(String id, String name) { this.id = id; this.name = name; }

    public String getId() { return id; }
    public String getName() { return name; }

    private static final Map<String, Mode> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(Mode::getId, m -> m));

    public static Mode fromId(String id) {
        Mode mode = BY_ID.get(id);
        if (mode == null)
            throw new IllegalArgumentException("Unknown mode id: " + id);
        return mode;
    }
}
