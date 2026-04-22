package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HouseStoreTest {

    @Mock private HouseRepository repository;

    private HouseStore houseStore;

    @BeforeEach
    void setUp() {
        Executor directExecutor = Runnable::run;
        houseStore = new HouseStore(repository, directExecutor);
    }

    @Test
    void add_addsHouseToMasterList() {
        House saved = new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER);
        when(repository.add(saved)).thenReturn(saved);

        ObservableHouse result = houseStore.add(saved);

        assertEquals(1L, result.getId());
        assertEquals("Home", result.getName());
        assertTrue(houseStore.getAll().contains(result));
        verify(repository).add(saved);
    }

    @Test
    void delete_removesHouseFromMasterList() {
        House saved = new House(2L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER);
        when(repository.get(2L)).thenReturn(saved);
        houseStore.get(2L);

        houseStore.delete(2L);

        assertTrue(houseStore.getAll().isEmpty());
        verify(repository).delete(2L);
    }

    @Test
    void refreshAllAsync_updatesExistingObservableInstance() {
        House initial = new House(3L, "Old", "Addr", ZoneId.of("UTC"), Role.GUEST);
        House updated = new House(3L, "New", "Addr2", ZoneId.of("Europe/London"), Role.OWNER);
        when(repository.get(3L)).thenReturn(initial);
        when(repository.getAll()).thenReturn(List.of(updated));

        ObservableHouse cached = houseStore.get(3L);
        houseStore.refreshAllAsync().join();

        assertSame(cached, houseStore.getAll().getFirst());
        assertEquals("New", cached.getName());
        assertEquals(ZoneId.of("Europe/London"), cached.getTimezone());
    }
}
