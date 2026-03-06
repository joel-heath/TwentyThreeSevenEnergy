package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.time.ZoneId;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStoreTest {

    @Mock private UserRepository userRepository;
    @Mock private HouseRepository houseRepository;
    @Mock private HouseStore houseStore;
    @Mock private SessionManager sessionManager;

    private final Executor syncExecutor = Runnable::run;

    private UserStore userStore;

    private Housemate housemate;
    private ObservableHouse house1;
    private ObservableHouse house2;

    @BeforeEach
    void setUp() {
        House domainHouse1 = new House(10L, "Awesome House", "12 Awesome Road", ZoneId.of("UTC"), Role.OWNER);
        House domainHouse2 = new House(20L, "TERRIBLE House", "-8 AWFUL Street", ZoneId.of("America/New_York"), Role.RESIDENT);
        house1 = new ObservableHouse(domainHouse1);
        house2 = new ObservableHouse(domainHouse2);

        housemate = new Housemate(1L, 10L, "John Doe", "john@example.com", Role.OWNER);
        Preferences preferences = new Preferences(
                1L,
                false,
                ColorVision.PROTAN,
                Theme.LIGHT_CONTRAST,
                Mode.SIMPLE,
                false,
                2.5,
                10L
        );

        when(userRepository.getCurrentPreferences()).thenReturn(preferences);
        when(houseStore.get(10L)).thenReturn(house1);
        when(houseRepository.getCurrentUserAsHousemate(10L)).thenReturn(housemate);

        userStore = new UserStore(
                userRepository,
                houseRepository,
                houseStore,
                sessionManager,
                syncExecutor
        );
    }

    @Test
    void shouldPopulateStateOnInitialization() {
        assertEquals(1L, userStore.getCurrent().getId());
        assertEquals("John Doe", userStore.getCurrent().getName());
        assertEquals(house1, userStore.getPreferences().getActiveHouse());

        verify(userRepository, times(1)).getCurrentPreferences();
        verify(houseStore, times(1)).get(10L);
    }

    @Test
    void shouldMapAndSaveUser() {
        userStore.getCurrent().setName("Jane Doe");
        userStore.getCurrent().setEmail("jane@example.com");

        userStore.saveUser();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).updateMe(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(1L, savedUser.id());
        assertEquals("Jane Doe", savedUser.name());
        assertEquals("jane@example.com", savedUser.email());
    }

    @Test
    void shouldSavePreferences() {
        when(houseRepository.getCurrentUserAsHousemate(anyLong())).thenReturn(housemate);

        userStore.getPreferences().setLargeFont(true);
        userStore.getPreferences().setVision(ColorVision.DEUTERAN);
        userStore.getPreferences().setTheme(Theme.DARK);
        userStore.getPreferences().setMode(Mode.ADVANCED);
        userStore.getPreferences().setShareLocation(true);
        userStore.getPreferences().setEnergyGoal(3.0);
        userStore.getPreferences().setActiveHouse(house2);

        userStore.savePreferences();

        ArgumentCaptor<Preferences> preferencesCaptor = ArgumentCaptor.forClass(Preferences.class);
        verify(userRepository).updateCurrentPreferences(preferencesCaptor.capture());

        Preferences savedPrefs = preferencesCaptor.getValue();
        assertEquals(1L, savedPrefs.userId());
        assertTrue(savedPrefs.largeFont());
        assertEquals(ColorVision.DEUTERAN, savedPrefs.vision());
        assertEquals(Theme.DARK, savedPrefs.theme());
        assertEquals(Mode.ADVANCED, savedPrefs.mode());
        assertTrue(savedPrefs.shareLocation());
        assertEquals(3.0, savedPrefs.energyGoal());
        assertEquals(20L, savedPrefs.activeHouseId());
    }

    @Test
    void shouldDeleteUser() {
        userStore.deleteUser("very-secure-password");

        verify(userRepository).deleteMe("very-secure-password");
    }

    @Test
    void shouldRefreshAsynchronously() {
        Preferences newPrefs = mock(Preferences.class);
        when(newPrefs.activeHouseId()).thenReturn(20L);

        ObservableHouse newHouse = new ObservableHouse(
                new House(20L, "New House", "456 Ave", ZoneId.of("UTC"), Role.RESIDENT)
        );
        Housemate newHousemate = new Housemate(1L, 20L, "John Doe", "john@example.com", Role.RESIDENT);

        when(userRepository.getCurrentPreferences()).thenReturn(newPrefs);
        when(houseStore.get(20L)).thenReturn(newHouse);
        when(houseRepository.getCurrentUserAsHousemate(20L)).thenReturn(newHousemate);

        userStore.refreshAsync().join();

        assertEquals(newHouse, userStore.getPreferences().getActiveHouse());
        assertEquals(20L, userStore.getCurrent().getHouse().getId());
        assertEquals(Role.RESIDENT, userStore.getCurrent().getRole());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRefreshOnSessionChange() {
        ArgumentCaptor<Consumer<Boolean>> subscriberCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(sessionManager).subscribe(subscriberCaptor.capture(), eq(false));

        Consumer<Boolean> sessionCallback = subscriberCaptor.getValue();

        sessionCallback.accept(true);

        // Once in Constructor, once in Session Callback
        verify(userRepository, times(2)).getCurrentPreferences();
    }

    @Test
    void shouldUpdateCurrentUserWhenActiveHouseChanges() {
        ObservableHouse newHouse = new ObservableHouse(
                new House(30L, "Summer Home", "Beach", ZoneId.of("UTC"), Role.OWNER)
        );
        Housemate summerHousemate = new Housemate(1L, 30L, "John Doe", "john@example.com", Role.OWNER);

        when(houseRepository.getCurrentUserAsHousemate(30L)).thenReturn(summerHousemate);

        userStore.getPreferences().setActiveHouse(newHouse);

        verify(houseRepository).getCurrentUserAsHousemate(30L);
        assertEquals(30L, userStore.getCurrent().getHouse().getId());
    }
}