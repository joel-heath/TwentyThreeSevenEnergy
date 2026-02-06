package uk.ac.soton.comp2300.group42.energyclient.data.api;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ColorVision {
    TYPICAL("typical"),
    PROTAN("protanopia"),
    DEUTERAN("deuteranopia"),
    TRITAN("tritanopia"),
    ACHROMA("achromatopsia");

    private final String id;

    ColorVision(String id) { this.id = id; }

    public String getId() { return id; }

    private static final Map<String, ColorVision> BY_ID =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(ColorVision::getId, m -> m));

    public static ColorVision fromId(String id) {
        ColorVision mode = BY_ID.get(id);
        if (mode == null)
            throw new IllegalArgumentException("Unknown mode id: " + id);
        return mode;
    }
}

