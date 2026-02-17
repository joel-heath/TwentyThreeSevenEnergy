module uk.ac.soton.comp2300.group42.energyclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires java.management;
    requires java.net.http;

    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.view.components to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.data.dto to com.fasterxml.jackson.databind;

    exports uk.ac.soton.comp2300.group42.energyclient;
}