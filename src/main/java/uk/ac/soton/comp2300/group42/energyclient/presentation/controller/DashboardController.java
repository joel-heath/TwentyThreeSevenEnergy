package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.DashboardViewModel;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane container;

    private final DashboardViewModel vm;
    @Inject public DashboardController(DashboardViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        vm.targetFxmlProperty().subscribe(this::loadView);
    }

    private void loadView(String targetFxml) {
        if (targetFxml == null || targetFxml.isEmpty())
            return;

        try {
            Parent view = Navigator.loadFXML(new Navigator.ViewContext(Navigator.DEFAULT_PATH + targetFxml, null));
            container.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + targetFxml, e);
        }
    }
}
