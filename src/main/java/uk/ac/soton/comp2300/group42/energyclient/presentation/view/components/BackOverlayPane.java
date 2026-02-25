package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;

import java.io.IOException;

public class BackOverlayPane extends StackPane {

    public BackOverlayPane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BackOverlayPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML private void onBack() {
        Navigator.goBack();
    }
}
