package uk.ac.soton.comp2300.group42.energyclient.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

import java.io.IOException;

public class SettingsOverlayPane extends StackPane {
    public SettingsOverlayPane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settingsOverlayPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML private void onSettings() {
        Navigator.goTo("settings.fxml");
    }
}
