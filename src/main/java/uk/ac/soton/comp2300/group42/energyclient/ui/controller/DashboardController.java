package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.DashboardViewModel;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane container;

    @FXML private void initialize() {
        Mode currentMode = vm.getPreferences().getMode();

        String targetFxml = (currentMode == Mode.ADVANCED)
                ? "AdvancedDashboard.fxml"
                : "SimpleDashboard.fxml";

        try {
            Parent view = Navigator.loadFXML(new Navigator.ViewContext(Navigator.defaultPath + targetFxml, null));
            container.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + targetFxml, e);
        }
    }

    private final DashboardViewModel vm;

    public DashboardController(DashboardViewModel vm) {
        this.vm = vm;
    }
}
