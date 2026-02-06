package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

public class LandingController {

    public void onLogin() {
        Navigator.goTo("Login.fxml");
    }

    public void onDive() {
        Navigator.goToIrreversible("Dashboard.fxml");
    }
}
