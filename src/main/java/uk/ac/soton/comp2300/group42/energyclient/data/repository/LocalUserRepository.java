package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

import java.util.Optional;

@Singleton
public class LocalUserRepository implements UserRepository {

    private final LocalStorageClient client;

    @Inject
    public LocalUserRepository(LocalStorageClient client) {
        this.client = client;
    }

    @Override
    public User getCurrentUser() {
        return null;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return null;
    }

    @Override
    public void saveUser(User user) {
        // client.saveUser(user);
    }
}