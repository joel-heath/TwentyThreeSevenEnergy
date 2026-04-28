package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Subscription;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.DashboardViewModel;

import java.io.IOException;

public class DashboardController {

    private final DashboardViewModel vm;
    private Subscription fxmlSub;
    private Subscription sceneSub;

    @FXML private StackPane container;

    @Inject public DashboardController(DashboardViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        fxmlSub = vm.targetFxmlProperty().subscribe(this::loadView);
        sceneSub = container.sceneProperty().subscribe((_, newScene) -> {
            if (newScene == null)
                dispose();
        });
    }

    private void dispose() {
        Subscription.combine(fxmlSub, sceneSub).unsubscribe();
        fxmlSub = null;
        sceneSub = null;
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
