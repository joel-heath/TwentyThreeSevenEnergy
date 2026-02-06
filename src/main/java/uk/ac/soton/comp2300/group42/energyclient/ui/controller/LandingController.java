package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

public class LandingController {

    @FXML
    private Parent mainContentArea;

    public void initialize() {
        mainContentArea.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                registerDebugShortcut(newScene);
            }
        });
    }

    private void registerDebugShortcut(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (new KeyCodeCombination(
                    KeyCode.D,
                    KeyCombination.CONTROL_DOWN,
                    KeyCombination.SHIFT_DOWN
            ).match(e)) {

                //openHiddenDebugScreen();
                Navigator.goTo("debug/dashboarddebug.fxml");

                e.consume();
            }
        });
    }

    public void onLogin() {
        Navigator.goTo("Login.fxml");
    }

    public void onDive() {
        Navigator.goToIrreversible("Dashboard.fxml");
    }
}
