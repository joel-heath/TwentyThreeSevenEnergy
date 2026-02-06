package uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

public class DashboardDebugController {

    public StackPane root;
    public VBox mainContentArea;

    public void onExit() {
        Navigator.goTo("landing.fxml");
    }

}
