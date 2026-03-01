package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.isDebugging;

public class LandingController {

    private static final KeyCodeCombination DEBUG_SHORTCUT = new KeyCodeCombination(
            KeyCode.D,
            KeyCombination.CONTROL_DOWN,
            KeyCombination.SHIFT_DOWN);

    @FXML private Parent mainContentArea;
    @FXML private Button debugButton;

    @FXML private void initialize() {
        if (isDebugging()) {
            mainContentArea.sceneProperty().addListener((_, _, newScene) -> {
                if (newScene != null) {
                    registerDebugShortcut(newScene);
                }
            });
            debugButton.setManaged(true);
            debugButton.setVisible(true);
        }
    }

    private void registerDebugShortcut(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (DEBUG_SHORTCUT.match(e)) {
                onDebug();
                e.consume();
            }
        });
    }

    @FXML private void onLogin() {
        Navigator.goTo("Login.fxml");
    }

    @FXML private void onDive() {
        Navigator.goToIrreversible("Dashboard.fxml");
    }

    @FXML private void onAccessibilitySettings() {
        Navigator.goTo("AccessibilitySettings.fxml");
    }

    @FXML private void onDebug() {
        Navigator.goTo("debug/DashboardDebug.fxml");
    }
}
