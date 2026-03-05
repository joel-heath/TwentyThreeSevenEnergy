module uk.ac.soton.comp2300.group42.energyclient {
    requires EnergyApiContracts;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.management;
    requires java.net.http;
    requires java.keyring;
    requires com.sun.jna;
    requires jdk.javadoc;
    requires com.google.guice;
    requires jakarta.inject;
    requires org.mapstruct;
    requires java.rmi;
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.databind;
    requires tools.jackson.core;

    opens uk.ac.soton.comp2300.group42.energyclient                               to javafx.graphics;
    opens uk.ac.soton.comp2300.group42.energyclient.data.backend                  to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.data.external                 to com.google.guice, tools.jackson.databind;
    opens uk.ac.soton.comp2300.group42.energyclient.data.local                    to com.google.guice, tools.jackson.databind;
    opens uk.ac.soton.comp2300.group42.energyclient.data.mapper                   to com.google.guice, org.mapstruct;
    opens uk.ac.soton.comp2300.group42.energyclient.data.repository               to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.data.security                 to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.di                            to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.domain.model                  to tools.jackson.databind;
    opens uk.ac.soton.comp2300.group42.energyclient.domain.service                to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.domain.session                to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.controller       to com.google.guice, javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.controller.debug to com.google.guice, javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.services         to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.store            to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.view.components  to javafx.fxml;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel        to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug  to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.observable       to com.google.guice;
    opens uk.ac.soton.comp2300.group42.energyclient.presentation.util             to com.google.guice;
}