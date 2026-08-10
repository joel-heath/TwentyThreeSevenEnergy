package uk.ac.soton.comp2300.group42.energyclient.di;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AppStateManager {

    private static final Injector currentInjector = Guice.createInjector(new CoreEnergyModule());

    public static Injector getInjector() {
        return currentInjector;
    }
}