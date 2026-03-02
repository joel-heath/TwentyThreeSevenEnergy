package uk.ac.soton.comp2300.group42.energyclient;

import javafx.application.Platform;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class EnergyClient extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Navigator.initialize(stage);

        stage.setWidth(337.5);
        stage.setHeight(600);

        stage.setMinWidth(225);
        stage.setMinHeight(400);

        stage.setTitle("23/7 Energy");

        stage.setOnCloseRequest(_ -> {
            // first make sure data is saved if using local storage?
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }
}