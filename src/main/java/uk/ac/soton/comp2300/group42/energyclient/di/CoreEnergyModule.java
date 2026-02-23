package uk.ac.soton.comp2300.group42.energyclient.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import uk.ac.soton.comp2300.group42.energyclient.data.repository.RemoteAuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.IDoEverything;

public class CoreEnergyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AuthRepository.class).to(RemoteAuthRepository.class);
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    PreferencesModel providePreferences(IDoEverything IDoEverything) {
        return IDoEverything.getPreferences();
    }
}