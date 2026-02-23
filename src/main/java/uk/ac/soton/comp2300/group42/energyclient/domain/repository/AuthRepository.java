package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

public interface AuthRepository {
    boolean isLoggedIn();
    boolean login(String email, String password);
    void logout();
    boolean register(String name, String email, String password);
}