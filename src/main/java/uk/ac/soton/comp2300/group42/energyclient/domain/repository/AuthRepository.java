package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

public interface AuthRepository {
    boolean verifyLoggedIn();
    void login(String email, String password);
    void logout();
    void register(String name, String email, String password);
}