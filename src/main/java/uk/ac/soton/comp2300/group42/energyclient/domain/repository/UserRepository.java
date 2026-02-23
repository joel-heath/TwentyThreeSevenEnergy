package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User getCurrentUser();
    Optional<User> getUserById(Long id);
    void saveUser(User user);
}