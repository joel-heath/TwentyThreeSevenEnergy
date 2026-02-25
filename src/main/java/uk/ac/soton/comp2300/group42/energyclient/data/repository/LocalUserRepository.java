package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

@Singleton
public class LocalUserRepository implements UserRepository {

    private final LocalStorageClient client;

    @Inject
    public LocalUserRepository(LocalStorageClient client) {
        this.client = client;
    }

    @Override
    public User getCurrent() {
        return null;
    }

    @Override
    public Preferences getCurrentPreferences() {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}