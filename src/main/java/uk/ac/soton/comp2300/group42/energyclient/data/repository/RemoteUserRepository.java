package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.UserMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.user.DeleteUserRequest;

@Singleton
public class RemoteUserRepository implements UserRepository {

    private final UserClient client;
    private final UserMapper mapper;

    @Inject
    public RemoteUserRepository(UserClient client, UserMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public User getCurrent() {
        return mapper.toUser(client.fetchMe());
    }

    @Override
    public Preferences getCurrentPreferences() {
        return mapper.toPreferences(client.fetchMyPreferences());
    }

    @Override
    public Preferences updateCurrentPreferences(Preferences preferences) {
        return mapper.toPreferences(client.putMyPreferences(mapper.toUpdatePreferencesRequest(preferences)));
    }

    @Override
    public User get(Long id) {
        throw new UnsupportedOperationException("No API endpoint exists yet for this operation");
    }
    
    @Override
    public User updateMe(User user) {
        return mapper.toUser(client.putMe(mapper.toUpdateUserRequest(user)));
    }

    @Override
    public void deleteMe(String password) {
        client.deleteMe(new DeleteUserRequest(password));
    }
}