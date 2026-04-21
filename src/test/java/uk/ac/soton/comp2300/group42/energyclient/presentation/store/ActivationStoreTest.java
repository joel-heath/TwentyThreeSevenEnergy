package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivationStoreTest {

    @Mock private ActivationRepository repository;
    @Mock private ApplianceStore applianceStore;
    @Mock private ObservablePreferences preferences;

    private final Executor syncExecutor = Runnable::run;

    private ActivationStore activationStore;
    private ObservableAppliance mockAppliance;

    @BeforeEach
    void setUp() {
        House domainHouse = new House(100L, "Active House", "123 Main St", ZoneId.of("UTC"), Role.OWNER);
        ObservableHouse activeHouse = new ObservableHouse(domainHouse);

        Appliance domainAppliance = new Appliance(200L, 100L, "Fridge");
        mockAppliance = new ObservableAppliance(domainAppliance, activeHouse);

        lenient().when(preferences.getActiveHouse()).thenReturn(activeHouse);

        activationStore = new ActivationStore(
                repository,
                applianceStore,
                preferences,
                syncExecutor
        );
    }

    @Test
    void shouldAddActivationAndAddToMasterList() {
        Activation newActivation = new Activation(200L, 100L, LocalTime.of(14, 0), LocalDate.of(2025, 1, 1));
        Activation savedActivation = new Activation(1L, 200L, 100L, ActivationType.NON_RECURRING,
                LocalTime.of(14, 0), LocalDate.of(2025, 1, 1),
                null, null, null, null, null, null, null);

        when(repository.add(newActivation)).thenReturn(savedActivation);
        when(applianceStore.get(200L)).thenReturn(mockAppliance);

        ObservableActivation result = activationStore.add(newActivation);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ActivationType.NON_RECURRING, result.getActivationType());
        assertTrue(activationStore.getAll().contains(result), "Master list should contain the new activation");
        verify(repository).add(newActivation);
    }

    @Test
    void shouldGetActivationAndCacheIt() {
        Activation pojo = new Activation(2L, 200L, 100L, ActivationType.NON_RECURRING,
                LocalTime.of(10, 30), LocalDate.of(2025, 2, 1),
                null, null, null, null, null, null, null);

        when(repository.get(100L, 2L)).thenReturn(pojo);
        when(applianceStore.get(200L)).thenReturn(mockAppliance);

        ObservableActivation result1 = activationStore.get(2L);
        ObservableActivation result2 = activationStore.get(2L);

        verify(repository, times(2)).get(100L, 2L);
        assertSame(result1, result2, "Store should return the exact same observable instance from cache");
        assertEquals(1, activationStore.getAll().size(), "Master list should only contain one instance");
    }

    @Test
    void shouldUpdateActivation() {
        Activation updatedPojo = new Activation(3L, 200L, 100L, ActivationType.RECURRING,
                LocalTime.of(8, 0), null,
                true, true, true, true, true, false, false);

        when(repository.update(updatedPojo)).thenReturn(updatedPojo);
        when(applianceStore.get(200L)).thenReturn(mockAppliance);

        ObservableActivation result = activationStore.update(updatedPojo);

        verify(repository).update(updatedPojo);
        assertEquals(3L, result.getId());
        assertEquals(mockAppliance, result.getAppliance());
        assertEquals(LocalTime.of(8, 0), result.getActivationTime());
        assertNull(result.getActivationDate());
        assertEquals(ActivationType.RECURRING, result.getActivationType());
        assertTrue(result.isRecursMonday());
        assertTrue(result.isRecursTuesday());
        assertTrue(result.isRecursWednesday());
        assertTrue(result.isRecursThursday());
        assertTrue(result.isRecursFriday());
        assertFalse(result.isRecursSaturday());
        assertFalse(result.isRecursSunday());
    }

    @Test
    void shouldDeleteActivationAndRemoveFromMasterList() {
        Activation pojo = new Activation(4L, 200L, 100L, ActivationType.NON_RECURRING,
                LocalTime.of(12, 0), LocalDate.of(2025, 3, 1),
                null, null, null, null, null, null, null);

        when(repository.get(100L, 4L)).thenReturn(pojo);
        when(applianceStore.get(200L)).thenReturn(mockAppliance);

        ObservableActivation observable = activationStore.get(4L);
        assertTrue(activationStore.getAll().contains(observable));

        activationStore.delete(4L);

        verify(repository).delete(100L, 4L);
        assertFalse(activationStore.getAll().contains(observable), "Item should be removed from the master list");
    }

    @Test
    void shouldRefreshAllAsyncAndPopulateLists() {
        List<Activation> activations = List.of(
                new Activation(10L, 200L, 100L, ActivationType.NON_RECURRING, LocalTime.of(9, 0), LocalDate.now(), null, null, null, null, null, null, null),
                new Activation(11L, 200L, 100L, ActivationType.RECURRING, LocalTime.of(18, 0), null, true, false, false, false, false, false, false)
        );

        when(repository.getAll(100L)).thenReturn(activations);
        when(applianceStore.get(200L)).thenReturn(mockAppliance);

        activationStore.refreshAllAsync();

        verify(applianceStore).refreshAllAsync();
        verify(repository).getAll(100L);
        assertEquals(2, activationStore.getAll().size());

        assertEquals(10L, activationStore.getAll().get(0).getId());
        assertEquals(11L, activationStore.getAll().get(1).getId());
    }

    @Test
    void shouldUpdateExistingCachedItemsOnRefreshAllAsync() {
        Activation initialPojo = new Activation(
                12L, 200L, 100L, ActivationType.NON_RECURRING,
                LocalTime.of(7, 0), LocalDate.of(2025, 12, 25),
                null, null, null, null, null, null, null);

        when(repository.get(100L, 12L)).thenReturn(initialPojo);
        when(applianceStore.get(200L)).thenReturn(mockAppliance);

        ObservableActivation cachedActivation = activationStore.get(12L);

        Activation updatedPojo = new Activation(12L, 200L, 100L, ActivationType.RECURRING,
                LocalTime.of(8, 0), null,
                true, true, true, true, true, false, false);

        when(repository.getAll(100L)).thenReturn(List.of(updatedPojo));

        activationStore.refreshAllAsync();

        assertEquals(1, activationStore.getAll().size());
        assertSame(cachedActivation, activationStore.getAll().getFirst());
        assertEquals(12L, cachedActivation.getId());
        assertEquals(mockAppliance, cachedActivation.getAppliance());
        assertEquals(LocalTime.of(8, 0), cachedActivation.getActivationTime());
        assertNull(cachedActivation.getActivationDate());
        assertEquals(ActivationType.RECURRING, cachedActivation.getActivationType());
        assertTrue(cachedActivation.isRecursMonday());
        assertTrue(cachedActivation.isRecursTuesday());
        assertTrue(cachedActivation.isRecursWednesday());
        assertTrue(cachedActivation.isRecursThursday());
        assertTrue(cachedActivation.isRecursFriday());
        assertFalse(cachedActivation.isRecursSaturday());
        assertFalse(cachedActivation.isRecursSunday());
    }
}