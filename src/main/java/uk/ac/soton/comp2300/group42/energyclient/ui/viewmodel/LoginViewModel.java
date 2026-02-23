package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.IDoEverything;

public class LoginViewModel {

    private final UserClient client;

    @Inject public LoginViewModel(IDoEverything IDoEverything) {
        this.client = IDoEverything.getUserClient();
    }

    public boolean login(String email, String password) {
        return client.login(email, password);
    }

    public boolean register(String name, String email, String password) {
        return client.register(name, email, password);
    }

}
