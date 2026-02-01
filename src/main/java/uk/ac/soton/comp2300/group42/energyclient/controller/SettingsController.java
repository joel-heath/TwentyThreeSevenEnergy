package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

public class SettingsController {

    //private final SettingsViewModel vm;

    //public SettingsController(SettingsViewModel vm) { this.vm = vm; }

    @FXML private void onBack() { Navigator.goTo("dashboard.fxml"); }
}
