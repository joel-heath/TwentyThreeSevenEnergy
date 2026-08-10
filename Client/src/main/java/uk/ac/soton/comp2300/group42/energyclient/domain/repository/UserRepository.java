package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;

public interface UserRepository {
    User getCurrent();
    Preferences getCurrentPreferences();
    Preferences updateCurrentPreferences(Preferences preferences);
    User get(Long id);
    User updateMe(User user);
    void deleteMe(String password);
}