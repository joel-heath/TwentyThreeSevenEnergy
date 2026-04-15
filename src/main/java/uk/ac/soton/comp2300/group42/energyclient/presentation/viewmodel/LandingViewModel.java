package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.lang.management.ManagementFactory;

public class LandingViewModel {

    private final BooleanProperty isDebugMode;

    @Inject public LandingViewModel() {
        boolean isDebugging = ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .anyMatch(arg -> arg.contains("jdwp"));

        isDebugMode = new SimpleBooleanProperty(isDebugging);
    }

    public BooleanProperty isDebugModeProperty() { 
        return isDebugMode; 
    }
}