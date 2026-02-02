package uk.ac.soton.comp2300.group42.energyclient.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.*;


public class ColourSettings {
    private static final ObjectProperty<Paint> usageGradient = new SimpleObjectProperty<>(defaultGradient());

    private static final ObjectProperty<ColourVisionMode> visionMode = new SimpleObjectProperty<>(ColourVisionMode.DEFAULT);

    static {
        visionMode.addListener((_, _, mode) ->
                usageGradient.set(createGradientFor(mode))
        );
    }

    public static ObjectProperty<Paint> usageGradientProperty() {
        return usageGradient;
    }

    public static ObjectProperty<ColourVisionMode> visionModeProperty() {
        return visionMode;
    }

    private static Paint createGradientFor(ColourVisionMode mode) {
        return switch (mode) {
            case PROTANOPIA, DEUTERANOPIA -> protanDeutanGradient();
            case TRITANOPIA -> tritanopiaGradient();
            case DEFAULT -> defaultGradient();
        };
    }

    private static Paint defaultGradient() {
        return gradient("#2ECC71", "#FFC107", "#E74C3C");
    }

    private static Paint protanDeutanGradient() {
        return gradient("#0072B2", "#E69F00", "#D55E00");
    }

    private static Paint tritanopiaGradient() {
        return gradient("#009E73", "#F0E442", "#56B4E9");
    }

    private static Paint gradient(String c1, String c2, String c3) {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(c1)),
                new Stop(0.5, Color.web(c2)),
                new Stop(1.0, Color.web(c3))
        );
    }


}
