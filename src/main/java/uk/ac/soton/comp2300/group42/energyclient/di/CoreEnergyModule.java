package uk.ac.soton.comp2300.group42.energyclient.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.mapstruct.factory.Mappers;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.*;
import uk.ac.soton.comp2300.group42.energyclient.data.repository.*;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendApiRootUri;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.ExternalMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import java.net.URI;

public class CoreEnergyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AuthRepository.class).to(RemoteAuthRepository.class);
        bind(UserRepository.class).to(SwitchableUserRepository.class);
        bind(HouseRepository.class).to(SwitchableHouseRepository.class);
        bind(ApplianceRepository.class).to(SwitchableApplianceRepository.class);
        bind(ActivationRepository.class).to(SwitchableActivationRepository.class);
        bind(MetricRepository.class).to(SwitchableMetricRepository.class);
        bind(EnergyPriceRepository.class).to(EnergyPriceRepositoryImpl.class);
    }

    @Provides
    @Singleton
    @BackendMapper
    JsonMapper provideBackendObjectMapper() {
        return JsonMapper.builder().build();
    }

    @Provides
    @Singleton
    @ExternalMapper
    JsonMapper provideExternalObjectMapper() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Provides
    @Singleton
    @BackendApiRootUri
    URI provideApiRootUri() {
        return URI.create("http://localhost:8080/api/"); // in production will be something like "https://group42.ecs.soton.ac.uk/api/"
    }

    @Provides
    @Singleton
    UserMapper provideUserMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Provides
    @Singleton
    HouseMapper provideHouseMapper() {
        return Mappers.getMapper(HouseMapper.class);
    }

    @Provides
    @Singleton
    ApplianceMapper provideApplianceMapper() {
        return Mappers.getMapper(ApplianceMapper.class);
    }

    @Provides
    @Singleton
    ActivationMapper provideActivationMapper() {
        return Mappers.getMapper(ActivationMapper.class);
    }

    @Provides
    @Singleton
    MetricMapper provideMetricMapper() {
        return Mappers.getMapper(MetricMapper.class);
    }

    @Provides
    @Singleton
    UnitRateMapper provideUnitRateMapper() {
        return Mappers.getMapper(UnitRateMapper.class);
    }

    @Provides
    @Singleton
    ObservableHousemate provideCurrentUser(UserStore userStore) {
        return userStore.getCurrent();
    }

    @Provides
    @Singleton
    ObservablePreferences providePreferences(UserStore userStore) {
        return userStore.getPreferences();
    }
}