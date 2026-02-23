package uk.ac.soton.comp2300.group42.energyclient.domain.session;

import com.google.inject.Singleton;

@Singleton
public class SessionManager {
    private boolean loggedIn = false;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}