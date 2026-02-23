package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

public class LocalUserRepository implements UserRepository {
    private final LocalStorageClient client;

    public LocalUserRepository(LocalStorageClient client) {
        this.client = client;
    }

    @Override
    public User getUserByEmail(String email) {
        // return client.queryUserByEmail(email);
        return null;
    }

    @Override
    public void saveUser(User user) {
        // client.saveUser(user);
    }
}