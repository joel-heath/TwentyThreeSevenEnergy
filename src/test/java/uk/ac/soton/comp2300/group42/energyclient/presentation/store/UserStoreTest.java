package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.time.ZoneId;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserStoreTest {

    @Mock private UserRepository userRepository;
    @Mock private HouseRepository houseRepository;
    @Mock private HouseStore houseStore;

    private UserStore userStore;
    private ObservableHouse house1;
    private ObservableHouse house2;

    @BeforeEach
    void setUp() {
        Executor directExecutor = Runnable::run;

        house1 = new ObservableHouse(new House(10L, "House 1", "Addr 1", ZoneId.of("UTC"), Role.OWNER));
        house2 = new ObservableHouse(new House(20L, "House 2", "Addr 2", ZoneId.of("Europe/London"), Role.RESIDENT));

        Preferences prefs = new Preferences(
            1L,
            false,
            ColorVision.TYPICAL,
            Theme.LIGHT,
            Mode.SIMPLE,
            false,
            1.0,
            10L
        );

        Housemate meInHouse1 = new Housemate(1L, 10L, "Alice", "alice@example.com", Role.OWNER);
        Housemate meInHouse2 = new Housemate(1L, 20L, "Alice", "alice@example.com", Role.RESIDENT);

        when(userRepository.getCurrentPreferences()).thenReturn(prefs);
        when(houseStore.get(10L)).thenReturn(house1);
        when(houseStore.get(20L)).thenReturn(house2);
        when(houseRepository.getCurrentUserAsHousemate(10L)).thenReturn(meInHouse1);
        when(houseRepository.getCurrentUserAsHousemate(20L)).thenReturn(meInHouse2);
        when(houseRepository.getCurrentUserAsHousemate(anyLong())).thenAnswer(invocation -> {
            long houseId = invocation.getArgument(0, Long.class);
            return new Housemate(1L, houseId, "Alice", "alice@example.com", Role.RESIDENT);
        });

        userStore = new UserStore(userRepository, houseRepository, houseStore, directExecutor);
        userStore.refreshAsync().join();
        clearInvocations(userRepository, houseRepository, houseStore);
    }

    @Test
    void saveUser_mapsObservableStateToDomainUser() {
        userStore.getCurrent().setName("Jane");
        userStore.getCurrent().setEmail("jane@example.com");

        userStore.saveUser();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).updateMe(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals(1L, saved.id());
        assertEquals("Jane", saved.name());
        assertEquals("jane@example.com", saved.email());
    }

    @Test
    void savePreferences_persistsCurrentPreferences() {
        userStore.savePreferences();

        ArgumentCaptor<Preferences> prefsCaptor = ArgumentCaptor.forClass(Preferences.class);
        verify(userRepository).updateCurrentPreferences(prefsCaptor.capture());
        assertEquals(10L, prefsCaptor.getValue().activeHouseId());
    }

    @Test
    void refreshAsync_updatesCurrentHouseAndRole() {
        Preferences newPrefs = new Preferences(
            1L,
            true,
            ColorVision.PROTAN,
            Theme.DARK,
            Mode.ADVANCED,
            true,
            2.5,
            20L
        );

        when(userRepository.getCurrentPreferences()).thenReturn(newPrefs);

        userStore.refreshAsync().join();

        assertSame(house2, userStore.getPreferences().getActiveHouse());
        assertEquals(20L, userStore.getCurrent().getHouse().getId());
        assertEquals(Role.RESIDENT, userStore.getCurrent().getRole());
    }

    @Test
    void deleteUser_delegatesToRepository() {
        userStore.deleteUser("password123");

        verify(userRepository).deleteMe("password123");
    }
}
