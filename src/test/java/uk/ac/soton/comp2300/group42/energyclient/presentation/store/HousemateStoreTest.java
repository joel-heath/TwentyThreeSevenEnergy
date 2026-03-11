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
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HousemateStoreTest {

    @Mock private HouseRepository repository;
    @Mock private HouseStore houseStore;
    @Mock private ObservablePreferences preferences;
    @Mock private SessionManager sessionManager;

    private final Executor syncExecutor = Runnable::run;

    private HousemateStore housemateStore;
    private ObservableHouse activeHouse;

    @BeforeEach
    void setUp() {
        House domainHouse = new House(100L, "Active House", "123 Main St", ZoneId.of("UTC"), Role.OWNER);
        activeHouse = new ObservableHouse(domainHouse);

        lenient().when(preferences.getActiveHouse()).thenReturn(activeHouse);

        housemateStore = new HousemateStore(
                repository,
                houseStore,
                preferences,
                sessionManager,
                syncExecutor
        );
    }

    @Test
    void shouldInviteHousemateAndAddToMasterList() {
        Housemate newHousemate = new Housemate(1L, 100L, "Alice", "alice@example.com", Role.RESIDENT);
        when(houseStore.get(100L)).thenReturn(activeHouse);

        ObservableHousemate result = housemateStore.invite(newHousemate);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals(Role.RESIDENT, result.getRole());
        assertEquals(activeHouse, result.getHouse());
        
        assertTrue(housemateStore.getAll().contains(result), "Master list should contain the invited housemate");
        // verify(repository).inviteHousemate(100L, newHousemate);
    }

    @Test
    void shouldKickHousemateAndRemoveFromMasterList() {
        Housemate newHousemate = new Housemate(2L, 100L, "Bob", "bob@example.com", Role.GUEST);
        when(houseStore.get(100L)).thenReturn(activeHouse);
        
        ObservableHousemate observable = housemateStore.invite(newHousemate);
        assertTrue(housemateStore.getAll().contains(observable));

        housemateStore.kick(2L);

        assertFalse(housemateStore.getAll().contains(observable), "Item should be removed from the master list");
        // verify(repository).kickHousemate(100L, 2L);
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionOnGet() {
        assertThrows(UnsupportedOperationException.class, () -> housemateStore.get(1L));
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionOnUpdate() {
        Housemate housemate = new Housemate(1L, 100L, "Test", "test@test.com", Role.RESIDENT);
        assertThrows(UnsupportedOperationException.class, () -> housemateStore.update(housemate));
    }

    @Test
    void shouldRefreshAllAsyncAndPopulateLists() {
        List<Housemate> housemates = List.of(
                new Housemate(10L, 100L, "Charlie", "charlie@example.com", Role.OWNER),
                new Housemate(11L, 100L, "Dave", "dave@example.com", Role.RESIDENT)
        );

        when(repository.getHousemates(100L)).thenReturn(housemates);

        housemateStore.refreshAllAsync();

        verify(repository).getHousemates(100L);
        assertEquals(2, housemateStore.getAll().size());

        ObservableHousemate charlie = housemateStore.getAll().getFirst();
        assertEquals(10L, charlie.getId());
        assertEquals("Charlie", charlie.getName());
        assertEquals("charlie@example.com", charlie.getEmail());
        assertEquals(Role.OWNER, charlie.getRole());

        ObservableHousemate dave = housemateStore.getAll().get(1);
        assertEquals(11L, dave.getId());
        assertEquals("Dave", dave.getName());
        assertEquals("dave@example.com", dave.getEmail());
        assertEquals(Role.RESIDENT, dave.getRole());
    }

    @Test
    void shouldUpdateExistingCachedItemsOnRefreshAllAsync() {
        Housemate initialPojo = new Housemate(12L, 100L, "Eve (Old)", "eve.old@example.com", Role.GUEST);
        when(houseStore.get(100L)).thenReturn(activeHouse);
        
        ObservableHousemate cachedHousemate = housemateStore.invite(initialPojo);

        Housemate updatedPojo = new Housemate(12L, 100L, "Eve (New)", "eve.new@example.com", Role.RESIDENT);
        when(repository.getHousemates(100L)).thenReturn(List.of(updatedPojo));

        housemateStore.refreshAllAsync();

        assertEquals(1, housemateStore.getAll().size());
        assertSame(cachedHousemate, housemateStore.getAll().getFirst(), "Should update the exact cached instance");
        
        assertEquals(12L, cachedHousemate.getId());
        assertEquals("Eve (New)", cachedHousemate.getName());
        assertEquals("eve.new@example.com", cachedHousemate.getEmail());
        assertEquals(Role.RESIDENT, cachedHousemate.getRole());
        assertEquals(activeHouse, cachedHousemate.getHouse());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldClearCacheAndMasterListOnSessionChange() {
        Housemate pojo = new Housemate(1L, 100L, "Frank", "frank@example.com", Role.RESIDENT);
        when(houseStore.get(100L)).thenReturn(activeHouse);
        housemateStore.invite(pojo);
        
        assertFalse(housemateStore.getAll().isEmpty());

        ArgumentCaptor<Consumer<Boolean>> subscriberCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(sessionManager).subscribe(subscriberCaptor.capture());
        Consumer<Boolean> sessionCallback = subscriberCaptor.getValue();

        sessionCallback.accept(true);

        assertTrue(housemateStore.getAll().isEmpty(), "Master list should be empty after session change");
    }
}