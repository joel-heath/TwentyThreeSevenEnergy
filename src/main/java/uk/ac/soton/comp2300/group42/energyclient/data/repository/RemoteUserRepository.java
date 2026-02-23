package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import uk.ac.soton.comp2300.group42.energyclient.data.backend.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.user.UserResponse;

public class RemoteUserRepository implements UserRepository {

    private final UserClient client;

    public RemoteUserRepository(UserClient client) {
        this.client = client;
    }

    @Override
    public User getCurrentUser() {
        UserResponse response = client.findCurrentUser();
        return new User(
                response.id(),
                response.name(),
                response.email());
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }
    
    @Override
    public void saveUser(User user) {

    }
}