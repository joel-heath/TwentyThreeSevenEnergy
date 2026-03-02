package uk.ac.soton.comp2300.group42.energyclient.domain.session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionManagerTest {

    @Test
    void testSessionManager() {
        SessionManager sessionManager = new SessionManager();

        assertFalse(sessionManager.isLoggedIn(), "User should not be logged in initially.");

        sessionManager.setLoggedIn(true);
        assertTrue(sessionManager.isLoggedIn(), "User should be logged in after setting to true.");

        sessionManager.setLoggedIn(false);
        assertFalse(sessionManager.isLoggedIn(), "User should not be logged in after setting to false.");
    }

}