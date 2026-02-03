package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.control.Button;
import uk.ac.soton.comp2300.group42.energyclient.model.ColourSettings;
import uk.ac.soton.comp2300.group42.energyclient.model.ColourVisionMode;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

public class SettingsController {
    private static String previousMode = "dashboard";
    @FXML private Button toggleButton;


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

        updateButtonText();
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

    @FXML private void toggleMode() {
        if (previousMode.equals("dashboard")) {
            Navigator.goTo("advanceddashboard.fxml");
            previousMode = "advanced";
        } else {
            Navigator.goTo("dashboard.fxml");
            previousMode = "dashboard";
        }
        updateButtonText();
    }

    @FXML private void updateButtonText() {
        if (previousMode.equals("dashboard")) {
            toggleButton.setText("Switch to Advanced Mode");
        } else {
            toggleButton.setText("Switch to Simple Mode");
        }
    }

    @FXML private void toggleShareLocation() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleColourblindTest() {
        ColourSettings.usageGradientProperty().set(colourblindGradient);
    }

}
