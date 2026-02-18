package uk.ac.soton.comp2300.group42.energyclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class EnergyClientModule extends AbstractModule {

    @Override
    protected void configure() {
        // bind(ApiClientInterface.class).to(RealApiClient.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    PreferencesModel providePreferences(Repository repository) {
        return repository.getPreferences();
    }
}