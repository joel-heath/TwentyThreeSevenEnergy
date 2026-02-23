package uk.ac.soton.comp2300.group42.energyclient.di;

import com.google.inject.AbstractModule;
import uk.ac.soton.comp2300.group42.energyclient.data.repository.LocalUserRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

public class LocalDataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(LocalUserRepository.class);
        // bind(HouseRepository.class).to(LocalHouseRepository.class);
        // etc.
    }
}