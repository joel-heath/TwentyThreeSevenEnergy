package uk.ac.soton.comp2300.group42.energyclient.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.energyclient.data.mapper.*;
import uk.ac.soton.comp2300.group42.energyclient.data.repository.*;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.IDoEverything;

public class CoreEnergyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AuthRepository.class).to(RemoteAuthRepository.class);
        bind(UserRepository.class).to(SwitchableUserRepository.class);
        bind(HouseRepository.class).to(SwitchableHouseRepository.class);
        bind(ApplianceRepository.class).to(SwitchableApplianceRepository.class);
        bind(ActivationRepository.class).to(SwitchableActivationRepository.class);
        bind(MetricRepository.class).to(SwitchableMetricRepository.class);
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Provides
    @Singleton
    public UserMapper provideUserMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Provides
    @Singleton
    public HouseMapper provideHouseMapper() {
        return Mappers.getMapper(HouseMapper.class);
    }

    @Provides
    @Singleton
    public ApplianceMapper provideApplianceMapper() {
        return Mappers.getMapper(ApplianceMapper.class);
    }

    @Provides
    @Singleton
    public ActivationMapper provideActivationMapper() {
        return Mappers.getMapper(ActivationMapper.class);
    }

    @Provides
    @Singleton
    public MetricMapper provideMetricMapper() { return Mappers.getMapper(MetricMapper.class); }

    @Provides
    @Singleton
    PreferencesModel providePreferences(IDoEverything IDoEverything) {
        return IDoEverything.getPreferences();
    }
}