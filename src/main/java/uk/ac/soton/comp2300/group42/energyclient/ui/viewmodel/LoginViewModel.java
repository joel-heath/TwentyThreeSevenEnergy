package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.ui.services.AuthService;

public class LoginViewModel {
    public LoginViewModel() {

    }

    public boolean login(String email, String password) {
        return AuthService.login(email, password);
    }

    public boolean register(String email, String password) {
        return AuthService.register(email, password);
    }

}
