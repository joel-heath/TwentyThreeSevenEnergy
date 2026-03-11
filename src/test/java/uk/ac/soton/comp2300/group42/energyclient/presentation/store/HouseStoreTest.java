package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseStoreTest {

    @Mock private HouseRepository repository;
    @Mock private SessionManager sessionManager;

    private final Executor syncExecutor = Runnable::run;

    private HouseStore houseStore;

    @BeforeEach
    void setUp() {
        houseStore = new HouseStore(
                repository,
                sessionManager,
                syncExecutor
        );
    }

    @Test
    void shouldAddEmptyHouseAndAddToMasterList() {
        House savedHouse = new House(1L, "New House", "New Address", ZoneId.of("UTC"), Role.OWNER);

        when(repository.add()).thenReturn(savedHouse);

        ObservableHouse result = houseStore.add();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New House", result.getName());
        assertEquals("New Address", result.getAddress());
        assertEquals(ZoneId.of("UTC"), result.getTimezone());
        assertEquals(Role.OWNER, result.getRole());
        assertTrue(houseStore.getAll().contains(result), "Master list should contain the new house");
        verify(repository).add();
    }

    @Test
    void shouldAddSpecificHouseAndAddToMasterList() {
        House newHouse = new House(null, "Specific House", "123 Specific St", ZoneId.of("Europe/London"), Role.OWNER);
        House savedHouse = new House(2L, "Specific House", "123 Specific St", ZoneId.of("Europe/London"), Role.OWNER);

        when(repository.add(newHouse)).thenReturn(savedHouse);

        ObservableHouse result = houseStore.add(newHouse);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Specific House", result.getName());
        assertEquals("123 Specific St", result.getAddress());
        assertEquals(ZoneId.of("Europe/London"), result.getTimezone());
        assertEquals(Role.OWNER, result.getRole());
        assertTrue(houseStore.getAll().contains(result), "Master list should contain the new house");
        verify(repository).add(newHouse);
    }

    @Test
    void shouldGetHouseAndCacheIt() {
        House pojo = new House(3L, "Cached House", "456 Cache Rd", ZoneId.of("UTC"), Role.RESIDENT);

        when(repository.get(3L)).thenReturn(pojo);

        ObservableHouse result1 = houseStore.get(3L);
        ObservableHouse result2 = houseStore.get(3L);

        verify(repository, times(2)).get(3L);
        assertSame(result1, result2, "Store should return the exact same observable instance from cache");
        assertEquals(1, houseStore.getAll().size(), "Master list should only contain one instance");
    }

    @Test
    void shouldUpdateHouse() {
        House initialPojo = new House(4L, "Old Name", "Old Address", ZoneId.of("UTC"), Role.GUEST);
        when(repository.get(4L)).thenReturn(initialPojo);
        ObservableHouse cachedHouse = houseStore.get(4L);

        House updatedPojo = new House(4L, "New Name", "New Address", ZoneId.of("America/New_York"), Role.OWNER);
        when(repository.update(updatedPojo)).thenReturn(updatedPojo);

        ObservableHouse result = houseStore.update(updatedPojo);

        verify(repository).update(updatedPojo);
        assertSame(cachedHouse, result, "Should return the exact cached instance updated with new values");
        
        assertEquals(4L, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("New Address", result.getAddress());
        assertEquals(ZoneId.of("America/New_York"), result.getTimezone());
        assertEquals(Role.OWNER, result.getRole());
    }

    @Test
    void shouldDeleteHouseAndRemoveFromMasterList() {
        House pojo = new House(5L, "Delete Me", "Nowhere", ZoneId.of("UTC"), Role.OWNER);

        when(repository.get(5L)).thenReturn(pojo);

        ObservableHouse observable = houseStore.get(5L);
        assertTrue(houseStore.getAll().contains(observable));

        houseStore.delete(5L);

        verify(repository).delete(5L);
        assertFalse(houseStore.getAll().contains(observable), "Item should be removed from the master list");
    }

    @Test
    void shouldLeaveHouseAndRemoveFromMasterList() {
        House pojo = new House(6L, "Leave Me", "Elsewhere", ZoneId.of("UTC"), Role.RESIDENT);

        when(repository.get(6L)).thenReturn(pojo);

        ObservableHouse observable = houseStore.get(6L);
        assertTrue(houseStore.getAll().contains(observable));

        houseStore.leave(6L);

        // verify(repository).leave(6L);
        assertFalse(houseStore.getAll().contains(observable), "Item should be removed from the master list");
    }

    @Test
    void shouldRefreshAllAsyncAndPopulateLists() {
        List<House> houses = List.of(
                new House(10L, "House A", "Address A", ZoneId.of("UTC"), Role.OWNER),
                new House(11L, "House B", "Address B", ZoneId.of("Europe/London"), Role.RESIDENT)
        );

        when(repository.getAll()).thenReturn(houses);

        houseStore.refreshAllAsync();

        verify(repository).getAll();
        assertEquals(2, houseStore.getAll().size());

        assertEquals(10L, houseStore.getAll().get(0).getId());
        assertEquals("House A", houseStore.getAll().get(0).getName());

        assertEquals(11L, houseStore.getAll().get(1).getId());
        assertEquals("House B", houseStore.getAll().get(1).getName());
    }

    @Test
    void shouldUpdateExistingCachedItemsOnRefreshAllAsync() {
        House initialPojo = new House(12L, "Stale Name", "Stale Address", ZoneId.of("UTC"), Role.RESIDENT);
        when(repository.get(12L)).thenReturn(initialPojo);

        ObservableHouse cachedHouse = houseStore.get(12L);

        House updatedPojo = new House(12L, "Fresh Name", "Fresh Address", ZoneId.of("Asia/Tokyo"), Role.OWNER);
        when(repository.getAll()).thenReturn(List.of(updatedPojo));

        houseStore.refreshAllAsync();

        assertEquals(1, houseStore.getAll().size());
        assertSame(cachedHouse, houseStore.getAll().getFirst());
        
        assertEquals(12L, cachedHouse.getId());
        assertEquals("Fresh Name", cachedHouse.getName());
        assertEquals("Fresh Address", cachedHouse.getAddress());
        assertEquals(ZoneId.of("Asia/Tokyo"), cachedHouse.getTimezone());
        assertEquals(Role.OWNER, cachedHouse.getRole());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldClearCacheAndMasterListOnSessionChange() {
        House pojo = new House(1L, "Session Test House", "123 Session St", ZoneId.of("UTC"), Role.OWNER);
        when(repository.get(1L)).thenReturn(pojo);
        houseStore.get(1L);

        assertFalse(houseStore.getAll().isEmpty());

        ArgumentCaptor<Consumer<Boolean>> subscriberCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(sessionManager).subscribe(subscriberCaptor.capture());
        Consumer<Boolean> sessionCallback = subscriberCaptor.getValue();

        sessionCallback.accept(true);

        assertTrue(houseStore.getAll().isEmpty(), "Master list should be empty after session change");
    }
}