module uk.ac.soton.comp2300.group42.energyclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires jdk.javadoc;
    requires java.management;
    requires java.net.http;
    requires com.google.guice;
    requires com.google.gson;

    opens uk.ac.soton.comp2300.group42.energyclient.data                to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.data.dto            to com.google.guice, com.fasterxml.jackson.databind;
    opens uk.ac.soton.comp2300.group42.energyclient.data.api            to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller       to com.google.guice, javafx.fxml, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug to com.google.guice, javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.model            to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.services         to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.util             to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.view.components  to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel        to com.google.guice, com.google.gson;

    exports uk.ac.soton.comp2300.group42.energyclient;
}