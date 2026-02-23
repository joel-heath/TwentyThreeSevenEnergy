package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;

public interface UserRepository {
    User getCurrentUser();
    User getUserById(Long id);
    void saveUser(User user);
}