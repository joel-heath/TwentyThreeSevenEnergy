module uk.ac.soton.comp2300.group42.energyclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens uk.ac.soton.comp2300.group42.energyclient.controller to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.view.components to javafx.fxml;

    exports uk.ac.soton.comp2300.group42.energyclient;
}