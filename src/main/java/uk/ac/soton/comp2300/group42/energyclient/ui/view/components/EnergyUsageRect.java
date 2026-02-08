package uk.ac.soton.comp2300.group42.energyclient.ui.view.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;

public class EnergyUsageRect {

    @FXML
    private Rectangle usageRect;
    private final Rectangle clip = new Rectangle();



    private static final double MAX_WIDTH = 250;

    private final DoubleProperty usage = new SimpleDoubleProperty(0);

    @FXML
    public void initialize() {
        clip.widthProperty().bind(
                usage.multiply(MAX_WIDTH)
        );
        clip.setHeight(25);
        usageRect.setClip(clip);
    }

    public DoubleProperty usageProperty() {
        return usage;
    }


}
