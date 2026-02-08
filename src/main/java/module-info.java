module uk.ac.soton.comp2300.group42.energyclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.management;

    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.view.components to javafx.fxml;

    exports uk.ac.soton.comp2300.group42.energyclient;
}