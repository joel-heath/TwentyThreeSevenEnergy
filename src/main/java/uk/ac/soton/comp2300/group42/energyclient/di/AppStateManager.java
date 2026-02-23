package uk.ac.soton.comp2300.group42.energyclient.di;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AppStateManager {

    private static Injector currentInjector;

    // Call this when the app starts offline or the user logs out
    public static void buildOfflineGraph() {
        currentInjector = Guice.createInjector(new CoreEnergyModule(), new LocalDataModule());
    }

    // Call this immediately after successful authentication
    public static void buildOnlineGraph() {
        currentInjector = Guice.createInjector(new CoreEnergyModule(), new RemoteDataModule());
    }

    public static Injector getInjector() {
        if (currentInjector == null)
            buildOfflineGraph();

        return currentInjector;
    }
}