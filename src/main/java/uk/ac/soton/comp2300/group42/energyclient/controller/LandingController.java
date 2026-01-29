package uk.ac.soton.comp2300.group42.energyclient.controller;

import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

public class LandingController {

    public void onLogin() {
        Navigator.goTo("login.fxml");
    }

    public void onDive() {
        Navigator.goToIrreversible("dashboard.fxml");
    }
}
