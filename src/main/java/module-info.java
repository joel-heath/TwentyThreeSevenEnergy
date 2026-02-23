module uk.ac.soton.comp2300.group42.energyclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.management;
    requires java.net.http;
    requires java.keyring;
    requires com.sun.jna;
    requires jdk.javadoc;
    requires com.fasterxml.jackson.databind;
    requires com.google.guice;
    requires com.google.gson;
    requires EnergyApiContracts;
    requires java.rmi;

    opens uk.ac.soton.comp2300.group42.energyclient.data                to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.data.backend to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.data.security       to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller       to com.google.guice, javafx.fxml, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug to com.google.guice, javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.services         to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.util             to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.view.components  to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel        to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.debug  to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.ui.model            to com.google.guice, com.google.gson;

    exports uk.ac.soton.comp2300.group42.energyclient;
    exports uk.ac.soton.comp2300.group42.energyclient.di;
    opens uk.ac.soton.comp2300.group42.energyclient.data.external to com.google.gson, com.google.guice;

}