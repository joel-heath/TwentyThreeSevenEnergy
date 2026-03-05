package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;

public class LoginViewModel {

    private final AuthRepository repo;

    @Inject
    public LoginViewModel(AuthRepository repo) {
        this.repo = repo;
    }

    public void login(String email, String password) {
        repo.login(email, password);
    }

    public void register(String name, String email, String password) {
        repo.register(name, email, password);
    }

}
