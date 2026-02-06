package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.scene.paint.*;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;

public class ColorSettings {

    private static final Paint defaultGradient = createGradient("#2ECC71", "#FFC107", "#E74C3C");
    private static final Paint protanDeutanGradient = createGradient("#0072B2", "#E69F00", "#D55E00");
    private static final Paint tritanopiaGradient = createGradient("#009E73", "#F0E442", "#56B4E9");
    private static final Paint achromaGradient = createGradient("#000000", "#808080", "#FFFFFF");

    public static Paint getGradientFor(ColorVision vision) {
        return switch (vision) {
            case PROTAN, DEUTERAN -> protanDeutanGradient;
            case TRITAN -> tritanopiaGradient;
            case ACHROMA -> achromaGradient;
            case TYPICAL -> defaultGradient;
        };
    }

    private static Paint createGradient(String c1, String c2, String c3) {
        return new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(c1)),
                new Stop(0.5, Color.web(c2)),
                new Stop(1.0, Color.web(c3))
        );
    }
}
