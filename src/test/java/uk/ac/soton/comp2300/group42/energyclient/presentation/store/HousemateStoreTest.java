package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.ZoneId;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HousemateStoreTest {

    @Mock private HouseRepository repository;
    @Mock private HouseStore houseStore;

    private HousemateStore housemateStore;

    @BeforeEach
    void setUp() {
        Executor directExecutor = Runnable::run;
        ObservableHouse house = new ObservableHouse(new House(100L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        ObservablePreferences preferences = new ObservablePreferences(new Preferences(), house);

        when(houseStore.get(100L)).thenReturn(house);

        housemateStore = new HousemateStore(repository, houseStore, preferences, directExecutor);
    }

    @Test
    void invite_addsHousemateToMasterList() {
        Housemate housemate = new Housemate(1L, 100L, "Alice", "alice@example.com", Role.RESIDENT);

        var result = housemateStore.invite(housemate);

        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
        assertTrue(housemateStore.getAll().contains(result));
    }

    @Test
    void kick_removesHousemateFromMasterList() {
        Housemate housemate = new Housemate(2L, 100L, "Bob", "bob@example.com", Role.GUEST);
        housemateStore.invite(housemate);

        housemateStore.kick(2L);

        assertTrue(housemateStore.getAll().isEmpty());
    }

    @Test
    void get_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> housemateStore.get(1L));
    }
}
