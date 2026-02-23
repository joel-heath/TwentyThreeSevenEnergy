package uk.ac.soton.comp2300.group42.energyclient.di;

import com.google.inject.AbstractModule;
import uk.ac.soton.comp2300.group42.energyclient.data.repository.RemoteUserRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;

public class RemoteDataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(RemoteUserRepository.class);
        // bind(HouseRepository.class).to(RemoteHouseRepository.class);
        // etc.
    }
}