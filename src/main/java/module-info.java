module uk.ac.soton.comp2300.group42.energyclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens uk.ac.soton.comp2300.group42.energyclient.controller to javafx.fxml;
    exports uk.ac.soton.comp2300.group42.energyclient;
}