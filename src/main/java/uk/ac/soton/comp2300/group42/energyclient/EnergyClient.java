package uk.ac.soton.comp2300.group42.energyclient;

import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class EnergyClient extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Navigator.initialize("landing.fxml", stage);

        stage.setWidth(337.5);
        stage.setHeight(600);

        stage.setMinWidth(225);
        stage.setMinHeight(400);

        stage.setTitle("23/7 Energy");
        stage.show();
    }
}