package uk.ac.soton.comp2300.group42.user;

public final class PasswordValidation {
    public static final String PASSWORD_QUALITY_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9\\s]).{8,}$";
    public static final String PASSWORD_QUALITY_MESSAGE =
            "Password must be at least 8 characters, contain at least one lowercase, one uppercase and one special character";

    private PasswordValidation() {
    }
}
