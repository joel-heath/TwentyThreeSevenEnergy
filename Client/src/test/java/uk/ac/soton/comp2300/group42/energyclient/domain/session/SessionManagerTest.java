package uk.ac.soton.comp2300.group42.energyclient.domain.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    void testSessionManagerBasicState() {
        assertFalse(sessionManager.isLoggedIn(), "User should not be logged in initially.");

        sessionManager.setLoggedIn(true);
        assertTrue(sessionManager.isLoggedIn(), "User should be logged in after setting to true.");

        sessionManager.setLoggedIn(false);
        assertFalse(sessionManager.isLoggedIn(), "User should not be logged in after setting to false.");
    }

    @Test
    void testSubscribeNotifiesImmediately() {
        AtomicBoolean receivedState = new AtomicBoolean(true);

        Consumer<Boolean> listener = receivedState::set;
        sessionManager.subscribe(listener);

        assertFalse(receivedState.get(), "Listener should immediately receive the current 'false' state upon subscription.");
    }

    @Test
    void testListenersNotifiedOnStateChange() {
        AtomicBoolean receivedState = new AtomicBoolean(false);
        AtomicInteger callCount = new AtomicInteger(0);

        Consumer<Boolean> listener = state -> {
            receivedState.set(state);
            callCount.incrementAndGet();
        };

        sessionManager.subscribe(listener);
        assertEquals(1, callCount.get(), "Listener should have been called while subscribing.");

        sessionManager.setLoggedIn(true);
        assertTrue(receivedState.get(), "Listener should receive 'true' after state change.");
        assertEquals(2, callCount.get(), "Listener should have been called twice.");

        sessionManager.setLoggedIn(false);
        assertFalse(receivedState.get(), "Listener should receive 'false' after state change.");
        assertEquals(3, callCount.get(), "Listener should have been called three times.");
    }

    @Test
    void testUnsubscribeStopsNotifications() {
        AtomicInteger callCount = new AtomicInteger(0);
        Consumer<Boolean> listener = _ -> callCount.incrementAndGet();

        sessionManager.subscribe(listener);
        assertEquals(1, callCount.get());

        sessionManager.unsubscribe(listener);
        sessionManager.setLoggedIn(true);

        assertEquals(1, callCount.get(), "Listener should not be called after unsubscribing.");
    }

    @Test
    void testMultipleListenersAreNotified() {
        AtomicBoolean state1 = new AtomicBoolean(false);
        AtomicBoolean state2 = new AtomicBoolean(false);

        sessionManager.subscribe(state1::set);
        sessionManager.subscribe(state2::set);

        sessionManager.setLoggedIn(true);

        assertTrue(state1.get(), "First listener should have received true.");
        assertTrue(state2.get(), "Second listener should have received true.");
    }
}