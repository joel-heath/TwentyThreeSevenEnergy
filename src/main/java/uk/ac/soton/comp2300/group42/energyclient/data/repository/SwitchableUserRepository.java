package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.Optional;

@Singleton
public class SwitchableUserRepository implements UserRepository {

    private final LocalUserRepository localRepository;
    private final RemoteUserRepository remoteRepository;
    private final SessionManager sessionManager;

    @Inject
    public SwitchableUserRepository(LocalUserRepository localRepository, RemoteUserRepository remoteRepository, SessionManager sessionManager) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
        this.sessionManager = sessionManager;
    }

    private UserRepository getActiveRepo() {
        return sessionManager.isLoggedIn() ? remoteRepository : localRepository;
    }

    @Override
    public User getCurrentUser() {
        return getActiveRepo().getCurrentUser();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return remoteRepository.getUserById(id);
    }

    @Override
    public void saveUser(User user) {
        getActiveRepo().saveUser(user);
    }
}
