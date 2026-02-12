package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.data.api.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class LoginViewModel {

    private final UserClient client;

    public LoginViewModel(Repository repository) {
        this.client = repository.getUserClient();
    }

    public boolean login(String email, String password) {
        return client.login(email, password);
    }

    public boolean register(String email, String password) {
        return client.register(email, password);
    }

}
