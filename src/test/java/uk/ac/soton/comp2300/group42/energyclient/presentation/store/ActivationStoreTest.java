package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivationStoreTest {

    @Mock private ActivationRepository repository;
    @Mock private ApplianceStore applianceStore;

    private ActivationStore activationStore;

    @BeforeEach
    void setUp() {
        Executor directExecutor = Runnable::run;
        ObservableHouse house = new ObservableHouse(new House(100L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        ObservablePreferences preferences = new ObservablePreferences(new Preferences(), house);
        ObservableAppliance appliance = new ObservableAppliance(new Appliance(200L, 100L, "Kettle"), house);

        when(applianceStore.get(200L)).thenReturn(appliance);

        activationStore = new ActivationStore(repository, applianceStore, preferences, directExecutor);
    }

    @Test
    void add_addsActivationToMasterList() {
        Activation activation = new Activation(1L, 200L, 100L, null, LocalTime.of(7, 30), LocalDate.now(), null, null, null, null, null, null, null);
        when(repository.add(activation)).thenReturn(activation);

        var result = activationStore.add(activation);

        assertEquals(1L, result.getId());
        assertTrue(activationStore.getAll().contains(result));
        verify(repository).add(activation);
    }

    @Test
    void delete_usesActiveHouseIdAndRemovesItem() {
        Activation activation = new Activation(2L, 200L, 100L, null, LocalTime.of(8, 0), LocalDate.now(), null, null, null, null, null, null, null);
        when(repository.get(100L, 2L)).thenReturn(activation);
        activationStore.get(2L);

        activationStore.delete(2L);

        assertTrue(activationStore.getAll().isEmpty());
        verify(repository).delete(100L, 2L);
    }

    @Test
    void refreshAllAsync_updatesExistingObservableInstance() {
        Activation initial = new Activation(3L, 200L, 100L, null, LocalTime.of(9, 0), LocalDate.now(), null, null, null, null, null, null, null);
        Activation updated = new Activation(3L, 200L, 100L, null, LocalTime.of(10, 0), LocalDate.now().plusDays(1), null, null, null, null, null, null, null);
        when(repository.get(100L, 3L)).thenReturn(initial);
        when(repository.getAll(100L)).thenReturn(List.of(updated));

        var cached = activationStore.get(3L);
        activationStore.refreshAllAsync().join();

        assertSame(cached, activationStore.getAll().getFirst());
        assertEquals(LocalTime.of(10, 0), cached.getActivationTime());
    }
}
