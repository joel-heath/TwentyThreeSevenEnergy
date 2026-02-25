package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.UserMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

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
    public User get(Long id) {
        throw new UnsupportedOperationException("No API endpoint exists yet for this operation");
    }
    
    @Override
    public User update(User user) {
        throw new UnsupportedOperationException("No API endpoint exists yet for this operation");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("No API endpoint exists yet for this operation");
    }
}