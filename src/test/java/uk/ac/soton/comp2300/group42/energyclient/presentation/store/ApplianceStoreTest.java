package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplianceStoreTest {

    @Mock private ApplianceRepository repository;
    @Mock private HouseStore houseStore;
    @Mock private ObservablePreferences preferences;
    @Mock private SessionManager sessionManager;

    private final Executor syncExecutor = Runnable::run;

    private ApplianceStore applianceStore;
    private ObservableHouse activeHouse;

    @BeforeEach
    void setUp() {
        House domainHouse = new House(100L, "Active House", "123 Main St", ZoneId.of("UTC"), Role.OWNER);
        activeHouse = new ObservableHouse(domainHouse);

        lenient().when(preferences.getActiveHouse()).thenReturn(activeHouse);

        applianceStore = new ApplianceStore(
                repository,
                houseStore,
                preferences,
                sessionManager,
                syncExecutor
        );
    }

    @Test
    void shouldAddApplianceAndAddToMasterList() {
        Appliance newAppliance = new Appliance(null, 100L, "New Fridge");
        Appliance savedAppliance = new Appliance(1L, 100L, "New Fridge");

        when(repository.add(newAppliance)).thenReturn(savedAppliance);
        when(houseStore.get(100L)).thenReturn(activeHouse);

        ObservableAppliance result = applianceStore.add(newAppliance);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Fridge", result.getName());
        assertTrue(applianceStore.getAll().contains(result), "Master list should contain the new appliance");
        verify(repository).add(newAppliance);
    }

    @Test
    void shouldGetApplianceAndCacheIt() {
        Appliance pojo = new Appliance(2L, 100L, "Washing Machine");
        
        when(repository.get(100L, 2L)).thenReturn(pojo);
        when(houseStore.get(100L)).thenReturn(activeHouse);

        ObservableAppliance result1 = applianceStore.get(2L);
        ObservableAppliance result2 = applianceStore.get(2L);

        verify(repository, times(2)).get(100L, 2L);
        assertSame(result1, result2, "Store should return the exact same observable instance from cache");
        assertEquals(1, applianceStore.getAll().size(), "Master list should only contain one instance");
    }

    @Test
    void shouldUpdateAppliance() {
        Appliance updatedPojo = new Appliance(3L, 100L, "Smart Oven");
        
        when(repository.update(updatedPojo)).thenReturn(updatedPojo);
        when(houseStore.get(100L)).thenReturn(activeHouse);

        ObservableAppliance result = applianceStore.update(updatedPojo);

        verify(repository).update(updatedPojo);
        assertEquals(3L, result.getId());
        assertEquals("Smart Oven", result.getName());
    }

    @Test
    void shouldDeleteApplianceAndRemoveFromMasterList() {
        Appliance pojo = new Appliance(4L, 100L, "Toaster");
        when(repository.get(100L, 4L)).thenReturn(pojo);
        when(houseStore.get(100L)).thenReturn(activeHouse);
        
        ObservableAppliance observable = applianceStore.get(4L);
        assertTrue(applianceStore.getAll().contains(observable));

        applianceStore.delete(4L);

        verify(repository).delete(100L, 4L);
        assertFalse(applianceStore.getAll().contains(observable), "Item should be removed from the master list");
    }

    @Test
    void shouldRefreshAllAsyncAndPopulateLists() {
        List<Appliance> appliances = List.of(
                new Appliance(10L, 100L, "Microwave"),
                new Appliance(11L, 100L, "Kettle")
        );

        when(repository.getAll(100L)).thenReturn(appliances);

        applianceStore.refreshAllAsync();

        verify(repository).getAll(100L);
        assertEquals(2, applianceStore.getAll().size());
        
        assertEquals(10L, applianceStore.getAll().get(0).getId());
        assertEquals("Microwave", applianceStore.getAll().get(0).getName());
        
        assertEquals(11L, applianceStore.getAll().get(1).getId());
        assertEquals("Kettle", applianceStore.getAll().get(1).getName());
    }

    @Test
    void shouldUpdateExistingCachedItemsOnRefreshAllAsync() {
        Appliance initialPojo = new Appliance(12L, 100L, "Old Heater");
        when(repository.get(100L, 12L)).thenReturn(initialPojo);
        when(houseStore.get(100L)).thenReturn(activeHouse);
        
        ObservableAppliance cachedAppliance = applianceStore.get(12L);

        Appliance updatedPojo = new Appliance(12L, 100L, "New Heater");
        when(repository.getAll(100L)).thenReturn(List.of(updatedPojo));

        applianceStore.refreshAllAsync();

        assertEquals(1, applianceStore.getAll().size());
        assertSame(cachedAppliance, applianceStore.getAll().getFirst());
        assertEquals("New Heater", cachedAppliance.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldClearCacheAndMasterListOnSessionChange() {
        Appliance pojo = new Appliance(1L, 100L, "TV");
        when(repository.get(100L, 1L)).thenReturn(pojo);
        when(houseStore.get(100L)).thenReturn(activeHouse);
        applianceStore.get(1L);
        
        assertFalse(applianceStore.getAll().isEmpty());

        ArgumentCaptor<Consumer<Boolean>> subscriberCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(sessionManager).subscribe(subscriberCaptor.capture());
        Consumer<Boolean> sessionCallback = subscriberCaptor.getValue();

        sessionCallback.accept(true);

        assertTrue(applianceStore.getAll().isEmpty(), "Master list should be empty after session change");
    }
}