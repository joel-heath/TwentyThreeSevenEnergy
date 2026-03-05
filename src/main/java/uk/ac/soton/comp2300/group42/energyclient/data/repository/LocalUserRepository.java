package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

import java.util.Objects;

import static uk.ac.soton.comp2300.group42.energyclient.data.repository.LocalRepositoryUtils.throwApiException;

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
            throwApiException(403, "Cannot access a different user");

        return data.user;
    }

    @Override
    public User update(User user) {
        if (Objects.isNull(user))
            throwApiException(400, "User is required");

        if (!Objects.equals(user.id(), data.user.id()))
            throwApiException(403, "Cannot update a different user");

        data.user = user;
        client.saveData();
        return data.user;
    }

    @Override
    public void delete(Long id) {
        if (!Objects.equals(id, data.user.id()))
            throwApiException(403, "Cannot delete a different user");

        throw new UnsupportedOperationException("No local storage implementation exists yet for this operation");
    }
}