package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.user.UserResponse;

import java.util.Optional;

@Singleton
public class RemoteUserRepository implements UserRepository {

    private final UserClient client;

    @Inject
    public RemoteUserRepository(UserClient client) {
        this.client = client;
    }

    @Override
    public User getCurrentUser() {
        return mapToUser(client.findCurrentUser());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.empty();
    }
    
    @Override
    public void saveUser(User user) {

    }

    public User mapToUser(UserResponse response) {
        return new User(
                response.id(),
                response.name(),
                response.email());
    }
}