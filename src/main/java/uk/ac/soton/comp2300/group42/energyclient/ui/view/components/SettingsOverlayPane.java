package uk.ac.soton.comp2300.group42.energyclient.ui.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

import java.io.IOException;

public class SettingsOverlayPane extends StackPane {

    public SettingsOverlayPane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsOverlayPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML private void onSettings() {
        Navigator.goTo("Settings.fxml");
    }
}
