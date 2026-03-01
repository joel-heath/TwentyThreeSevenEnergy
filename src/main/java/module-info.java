module uk.ac.soton.comp2300.group42.energyclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.management;
    requires java.net.http;
    requires java.keyring;
    requires com.sun.jna;
    requires jdk.javadoc;
    requires com.google.guice;
    requires com.google.gson;
    requires EnergyApiContracts;
    requires java.rmi;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.annotation;
    requires org.mapstruct;

    opens uk.ac.soton.comp2300.group42.energyclient.data.backend                  to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.data.external                 to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.data.local                    to com.google.guice, com.fasterxml.jackson.databind;
    opens uk.ac.soton.comp2300.group42.energyclient.data.mapper                   to com.google.guice, org.mapstruct;
    opens uk.ac.soton.comp2300.group42.energyclient.data.repository               to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.data.security                 to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.domain.model                  to com.fasterxml.jackson.databind;
    opens uk.ac.soton.comp2300.group42.energyclient.domain.session                to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.controller       to com.google.guice, javafx.fxml, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.controller.debug to com.google.guice, javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.services         to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.store            to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.util             to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.view.components  to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel        to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug  to com.google.guice, com.google.gson;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.observable to com.google.guice, com.google.gson;

    exports uk.ac.soton.comp2300.group42.energyclient;
    exports uk.ac.soton.comp2300.group42.energyclient.di;
    exports uk.ac.soton.comp2300.group42.energyclient.data.mapper                 to com.google.guice, org.mapstruct;
    exports uk.ac.soton.comp2300.group42.energyclient.domain.model                to com.google.guice, org.mapstruct;



}