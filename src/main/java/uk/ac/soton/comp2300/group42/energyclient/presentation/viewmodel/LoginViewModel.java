package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;

public class LoginViewModel {

    private final AuthRepository repo;

    @Inject public LoginViewModel(AuthRepository repo) {
        this.repo = repo;
    }

    public boolean login(String email, String password) {
        return repo.login(email, password);
    }

    public boolean register(String name, String email, String password) {
        return repo.register(name, email, password);
    }

}
