package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import uk.ac.soton.comp2300.group42.energyclient.data.api.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

public class RemoteUserRepository implements UserRepository {
    private final UserClient client;

    public RemoteUserRepository(UserClient client) {
        this.client = client;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }
    
    @Override
    public void saveUser(User user) {

    }
}