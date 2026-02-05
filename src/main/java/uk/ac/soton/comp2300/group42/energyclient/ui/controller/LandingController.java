package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

public class LandingController {

    public void onLogin() {
        Navigator.goTo("login.fxml");
    }

    public void onDive() {
        Navigator.goToIrreversible("dashboard.fxml");
    }
}
