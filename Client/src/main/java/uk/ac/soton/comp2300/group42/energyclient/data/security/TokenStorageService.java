package uk.ac.soton.comp2300.group42.energyclient.data.security;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import com.google.inject.Singleton;

@Singleton
public class TokenStorageService {

    private static final String APP_NAME = "EnergyClient";
    private static final String ACCOUNT_NAME = "refresh_token";
    private Keyring keyring;

    public TokenStorageService() {
        try {
            this.keyring = Keyring.create();
        } catch (BackendNotSupportedException e) {
            System.err.println("Secure storage backend not supported on this OS: " + e.getMessage());
            this.keyring = null;
        }
    }

    public void saveRefreshToken(String token) {
        if (keyring == null) return;
        try {
            keyring.setPassword(APP_NAME, ACCOUNT_NAME, token);
        } catch (PasswordAccessException e) {
            System.err.println("Failed to save token to OS Keychain: " + e.getMessage());
        }
    }

    public String getRefreshToken() {
        if (keyring == null) return null;
        try {
            return keyring.getPassword(APP_NAME, ACCOUNT_NAME);
        } catch (PasswordAccessException e) {
            // Will throw this if the token doesn't exist yet, which is expected on first run
            return null;
        }
    }

    public void clearRefreshToken() {
        if (keyring == null) return;
        try {
            keyring.deletePassword(APP_NAME, ACCOUNT_NAME);
        } catch (PasswordAccessException e) {
            System.err.println("Failed to delete token from OS Keychain: " + e.getMessage());
        }
    }
}
