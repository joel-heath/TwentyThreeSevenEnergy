package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.LandingViewModel;

public class LandingController {

    private static final KeyCodeCombination DEBUG_SHORTCUT = new KeyCodeCombination(
            KeyCode.D,
            KeyCombination.CONTROL_DOWN,
            KeyCombination.SHIFT_DOWN);

    @FXML private Parent mainContentArea;
    @FXML private Button debugButton;

    private final LandingViewModel vm;
    @Inject public LandingController(LandingViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        debugButton.visibleProperty().bind(vm.isDebugModeProperty());
        debugButton.managedProperty().bind(vm.isDebugModeProperty());
        mainContentArea.sceneProperty().subscribe(newScene -> {
            if (newScene != null && vm.isDebugModeProperty().get()) {
                newScene.getAccelerators().put(DEBUG_SHORTCUT, this::onDebug);
            }
        });
    }

    @FXML private void onLogin() { Navigator.goTo("Login.fxml"); }
    @FXML private void onDive() { Navigator.goToIrreversible("Dashboard.fxml"); }
    @FXML private void onAccessibilitySettings() { Navigator.goTo("AccessibilitySettings.fxml"); }
    @FXML private void onDebug() { Navigator.goTo("debug/DashboardDebug.fxml"); }
}