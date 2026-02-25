package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class ColorVisionManager {

    public enum ColorRole {
        STATUS_CHEAP,
        STATUS_AVERAGE,
        STATUS_EXPENSIVE,
        CARD_SURFACE,
        WIDGET_SURFACE,
        WIDGET_TEXT,
        TOGGLE_ENABLED,
        VALIDATION_ERROR
    }

    private record Palette(Paint gradient, Map<ColorRole, Color> colors) {
        private Palette {
            colors = Map.copyOf(colors);
        }
    }

    private static final ReadOnlyObjectWrapper<ColorVision> activeVision =
            new ReadOnlyObjectWrapper<>(ColorVision.TYPICAL);
    private static final ChangeListener<ColorVision> boundSourceListener =
            (_, _, newVision) -> setVision(newVision);

    private static ObservableValue<ColorVision> boundSource;

    private static final Map<ColorVision, Palette> PALETTES = createPalettes();

    private ColorVisionManager() {}

    public static synchronized void bind(ObservableValue<ColorVision> source) {
        Objects.requireNonNull(source, "source must not be null");
        unbind();
        boundSource = source;
        boundSource.addListener(boundSourceListener);
        setVision(boundSource.getValue());
    }

    public static synchronized void unbind() {
        if (boundSource != null) {
            boundSource.removeListener(boundSourceListener);
            boundSource = null;
        }
    }

    public static void setVision(ColorVision vision) {
        activeVision.set(vision == null ? ColorVision.TYPICAL : vision);
    }

    public static ColorVision getVision() {
        return activeVision.get();
    }

    public static ReadOnlyObjectProperty<ColorVision> visionProperty() {
        return activeVision.getReadOnlyProperty();
    }

    public static Paint getGradient() {
        return getGradientFor(getVision());
    }

    public static Paint getGradientFor(ColorVision vision) {
        return paletteFor(vision).gradient();
    }

    public static Color getColor(ColorRole role) {
        return getColor(getVision(), role);
    }

    public static Color getColor(ColorVision vision, ColorRole role) {
        Objects.requireNonNull(role, "role must not be null");
        return paletteFor(vision).colors().get(role);
    }

    public static String getWebColor(ColorRole role) {
        return getWebColor(getVision(), role);
    }

    public static String getWebColor(ColorVision vision, ColorRole role) {
        return toWeb(getColor(vision, role));
    }

    private static Palette paletteFor(ColorVision vision) {
        ColorVision resolvedVision = vision == null ? ColorVision.TYPICAL : vision;
        return PALETTES.getOrDefault(resolvedVision, PALETTES.get(ColorVision.TYPICAL));
    }

    private static Map<ColorVision, Palette> createPalettes() {
        EnumMap<ColorVision, Palette> palettes = new EnumMap<>(ColorVision.class);

        palettes.put(
                ColorVision.TYPICAL,
                new Palette(
                        createGradient("#2ECC71", "#FFC107", "#E74C3C"),
                        colors("#D4EDDA", "#FFF3CD", "#F8D7DA", "#ADD8E6", "#B0ACE3", "#477890", "#3797EF", "#DC3545")
                )
        );

        Palette protanDeutanPalette = new Palette(
                createGradient("#0072B2", "#E69F00", "#D55E00"),
                colors("#D9ECFA", "#FCE8C3", "#F8DCCB", "#CFE8FA", "#D7EAF9", "#3E5F77", "#0072B2", "#D55E00")
        );
        palettes.put(ColorVision.PROTAN, protanDeutanPalette);
        palettes.put(ColorVision.DEUTERAN, protanDeutanPalette);

        palettes.put(
                ColorVision.TRITAN,
                new Palette(
                        createGradient("#009E73", "#F0E442", "#56B4E9"),
                        colors("#D8F5EA", "#FFF8CC", "#F4DCEC", "#E5F2FB", "#EADFF5", "#3F5F70", "#009E73", "#CC79A7")
                )
        );

        palettes.put(
                ColorVision.ACHROMA,
                new Palette(
                        createGradient("#000000", "#808080", "#FFFFFF"),
                        colors("#E8E8E8", "#CFCFCF", "#A8A8A8", "#DDDDDD", "#D0D0D0", "#444444", "#4A4A4A", "#666666")
                )
        );

        return Map.copyOf(palettes);
    }

    private static Paint createGradient(String c1, String c2, String c3) {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(c1)),
                new Stop(0.5, Color.web(c2)),
                new Stop(1.0, Color.web(c3))
        );
    }

    private static Map<ColorRole, Color> colors(String statusCheap,
                                                 String statusAverage,
                                                 String statusExpensive,
                                                 String cardSurface,
                                                 String widgetSurface,
                                                 String widgetText,
                                                 String toggleEnabled,
                                                 String validationError) {
        return Map.of(
                ColorRole.STATUS_CHEAP, color(statusCheap),
                ColorRole.STATUS_AVERAGE, color(statusAverage),
                ColorRole.STATUS_EXPENSIVE, color(statusExpensive),
                ColorRole.CARD_SURFACE, color(cardSurface),
                ColorRole.WIDGET_SURFACE, color(widgetSurface),
                ColorRole.WIDGET_TEXT, color(widgetText),
                ColorRole.TOGGLE_ENABLED, color(toggleEnabled),
                ColorRole.VALIDATION_ERROR, color(validationError)
        );
    }

    private static Color color(String hex) {
        return Color.web(hex);
    }

    private static String toWeb(Color color) {
        int red = (int) Math.round(color.getRed() * 255);
        int green = (int) Math.round(color.getGreen() * 255);
        int blue = (int) Math.round(color.getBlue() * 255);
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
