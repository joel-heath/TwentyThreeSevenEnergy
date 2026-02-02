package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import uk.ac.soton.comp2300.group42.energyclient.model.ColourSettings;
import uk.ac.soton.comp2300.group42.energyclient.model.ColourVisionMode;

public class SettingsController {


    LinearGradient colourblindGradient = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0.0, Color.web("#0072B2")),
        new Stop(0.5, Color.web("#E69F00")),
        new Stop(1.0, Color.web("#D55E00"))
    );

    @FXML
    private ComboBox<ColourVisionMode> visionModeCombo;

    @FXML
    public void initialize() {
        visionModeCombo.getItems().setAll(ColourVisionMode.values());

        visionModeCombo.valueProperty().bindBidirectional(
                ColourSettings.visionModeProperty()
        );
    }

    @FXML private void toggleLargeFont() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleDarkMode() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleHighContrast() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleGrayscale() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleAdvancedMode() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleShareLocation() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleColourblindTest() {
        ColourSettings.usageGradientProperty().set(colourblindGradient);
    }

}
