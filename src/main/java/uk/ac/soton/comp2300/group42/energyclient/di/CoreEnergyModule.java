package uk.ac.soton.comp2300.group42.energyclient.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import javafx.application.Platform;
import org.mapstruct.factory.Mappers;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.*;
import uk.ac.soton.comp2300.group42.energyclient.data.repository.*;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.*;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.AppStateOrchestrator;

import java.net.URI;
import java.nio.file.Path;
import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreEnergyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AppStateOrchestrator.class).asEagerSingleton();
        bind(AuthRepository.class).to(RemoteAuthRepository.class);
        bind(UserRepository.class).to(SwitchableUserRepository.class);
        bind(HouseRepository.class).to(SwitchableHouseRepository.class);
        bind(ApplianceRepository.class).to(SwitchableApplianceRepository.class);
        bind(ActivationRepository.class).to(SwitchableActivationRepository.class);
        bind(MetricRepository.class).to(SwitchableMetricRepository.class);
        bind(EnergyPriceRepository.class).to(EnergyPriceRepositoryImpl.class);
        bind(WeatherRepository.class).to(WeatherRepositoryImpl.class);
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
    URI provideBackendApiRootUri() {
        return URI.create("http://localhost:8080/api/"); // in production will be something like "https://group42.ecs.soton.ac.uk/api/"
    }

    @Provides
    @Singleton
    @EnergyPriceApiRootUri
    URI provideEnergyPriceApiRootUri() {
        return URI.create("https://api.octopus.energy/v1/products/AGILE-18-02-21/electricity-tariffs/E-1R-AGILE-18-02-21-A/standard-unit-rates/");
    }

    @Provides
    @Singleton
    @WeatherApiRootUri
    URI provideWeatherApiRootUri() {
        return URI.create("https://api.open-meteo.com/v1/forecast/");
    }

    @Provides
    @Singleton
    @LocationApiRootUri
    URI provideLocationApiRootUri() {
        return URI.create("http://ip-api.com/json/");
    }

    @Provides
    @Singleton
    Clock provideClock() {
        return Clock.systemUTC();
    }

    @Provides
    @Singleton
    @LocalStoragePath
    Path provideLocalStoragePath() {
        return Path.of("local_data.json");
    }

    @Provides
    @Singleton
    @LocalStorageExecutor
    ExecutorService provideLocalStorageExecutor() {
        return Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("LocalStorage-Executor");
            return thread;
        });
    }

    @Provides
    @Singleton
    @UIExecutor
    Executor provideUIExecutor() {
        return Platform::runLater;
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
    WeatherEntryMapper provideWeatherEntryMapper() {
        return Mappers.getMapper(WeatherEntryMapper.class);
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