package uk.ac.soton.comp2300.group42.energyclient.domain.session;

import com.google.inject.Singleton;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Singleton
public class SessionManager {
    
    private boolean loggedIn = false;

    private final List<Consumer<Boolean>> sessionListeners = new CopyOnWriteArrayList<>();

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        System.out.println(loggedIn
                ? "Logged in, using remote repositories."
                : "Not logged in, using local storage repositories.");

        this.loggedIn = loggedIn;
        notifyListeners();
    }

    public void subscribe(Consumer<Boolean> listener) {
        subscribe(listener, true);
    }

    public void subscribe(Consumer<Boolean> listener, boolean notifyImmediately) {
        sessionListeners.add(listener);
        if (notifyImmediately)
            listener.accept(this.loggedIn);
    }

    public void unsubscribe(Consumer<Boolean> listener) {
        sessionListeners.remove(listener);
    }

    private void notifyListeners() {
        for (Consumer<Boolean> listener : sessionListeners) {
            listener.accept(loggedIn);
        }
    }
}