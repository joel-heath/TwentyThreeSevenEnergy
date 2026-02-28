package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

import java.util.Objects;

@Singleton
public class LocalUserRepository implements UserRepository {

    private final LocalStorageClient client;
    private final LocalStorageData data;

    @Inject
    public LocalUserRepository(LocalStorageClient client) {
        this.client = client;
        this.data = client.getData();
    }

    @Override
    public User getCurrent() {
        return data.user;
    }

    @Override
    public Preferences getCurrentPreferences() {
        return data.preferences;
    }

    @Override
    public User get(Long id) {
        if (!Objects.equals(id, data.user.id()))
            throw new UnsupportedOperationException("No local storage implementation exists yet for this operation");

        return data.user;
    }

    @Override
    public User update(User user) {
        if (Objects.isNull(user))
            throw new ApiException("User is required", 400);

        if (!Objects.equals(user.id(), data.user.id()))
            throw new UnsupportedOperationException("No local storage implementation exists yet for this operation");

        data.user = user;
        client.saveData();
        return data.user;
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("No local storage implementation exists yet for this operation");
    }
}