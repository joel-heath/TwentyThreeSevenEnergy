package uk.ac.soton.comp2300.group42.energyclient.controller;

import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

public class LandingController {

    public void onLogin() {
        Navigator.goTo("/uk/ac/soton/comp2300/group42/energyclient/view/login.fxml");
    }

    public void onDive() {
        Navigator.goTo("/uk/ac/soton/comp2300/group42/energyclient/view/dashboard.fxml");
    }
}
