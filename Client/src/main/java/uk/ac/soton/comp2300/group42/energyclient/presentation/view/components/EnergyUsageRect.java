package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class EnergyUsageRect extends StackPane {

    public enum UsageState {
        NORMAL,
        WARNING,
        CRITICAL
    }

    @FXML private Rectangle usageRect;
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final Rectangle clip = new Rectangle();
    private static final double MAX_WIDTH = 250;

    public EnergyUsageRect() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EnergyUsageRect.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML private void initialize() {
        clip.widthProperty().bind(usage.multiply(MAX_WIDTH));
        clip.setHeight(25);
        usageRect.setClip(clip);
    }

    public ObjectProperty<Paint> fillProperty() { return usageRect.fillProperty(); }
    public DoubleProperty usageProperty() { return usage; }

    public void setUsageState(UsageState state) {
        var classes = usageRect.getStyleClass();
        classes.remove("usage-gradient");
        classes.remove("usage-warning");
        classes.remove("usage-critical");

        if (state == null) {
            classes.add("usage-gradient");
            return;
        }

        switch (state) {
            case WARNING -> classes.add("usage-warning");
            case CRITICAL -> classes.add("usage-critical");
            case NORMAL -> classes.add("usage-gradient");
        }
    }
}
