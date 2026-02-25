package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import javafx.scene.paint.Paint;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;

public class ColorSettings {

    private ColorSettings() {}

    public static Paint getGradientFor(ColorVision vision) {
        return ColorVisionManager.getGradientFor(vision);
    }
}
