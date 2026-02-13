package uk.ac.soton.comp2300.group42.energyclient.data.api;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ColorVision {
    
    TYPICAL("typical", "Typical"),
    PROTAN("protanopia", "Protanopia"),
    DEUTERAN("deuteranopia", "Deuteranopia"),
    TRITAN("tritanopia", "Tritanopia"),
    ACHROMA("achromatopsia", "Achromatopsia");

    private final String id;
    private final String name;

    ColorVision(String id, String name) { this.id = id; this.name = name; }

    public String getId() { return id; }
    public String getName() { return name; }

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

