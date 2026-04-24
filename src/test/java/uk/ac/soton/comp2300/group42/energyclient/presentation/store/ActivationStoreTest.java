package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActivationStoreTest {
    @Mock private ActivationRepository repository;
    @Mock private ApplianceStore applianceStore;
    @Mock private ObservablePreferences preferences;
    private ActivationStore activationStore;

    private final Long HOUSE_ID = 100L;
    private final Long APPLIANCE_ID = 200L;

    @BeforeEach
    void setUp() {
        Executor directExecutor = Runnable::run;
        ObservableHouse house = new ObservableHouse(new House(HOUSE_ID, "H", "A", ZoneId.of("UTC"), Role.OWNER));

        when(preferences.getActiveHouse()).thenReturn(house);
        when(applianceStore.get(APPLIANCE_ID)).thenReturn(new ObservableAppliance(new Appliance(APPLIANCE_ID, HOUSE_ID, "Kettle"), house));

        activationStore = new ActivationStore(repository, applianceStore, preferences, directExecutor);
    }

    @Test
    @DisplayName("refreshAllAsync - Branch Coverage: New Item vs Cached Item")
    void refreshAllAsync_BranchCoverage() {
        Activation pojo = new Activation(1L, APPLIANCE_ID, HOUSE_ID, null, LocalTime.NOON, LocalDate.now(), null, null, null, null, null, null, null);
        when(repository.getAll(HOUSE_ID)).thenReturn(List.of(pojo));

        activationStore.refreshAllAsync().join();
        var firstInstance = activationStore.getAll().getFirst();
        assertEquals(LocalTime.NOON, firstInstance.getActivationTime());

        Activation updatedPojo = new Activation(1L, APPLIANCE_ID, HOUSE_ID, null, LocalTime.MIDNIGHT, LocalDate.now(), null, null, null, null, null, null, null);
        when(repository.getAll(HOUSE_ID)).thenReturn(List.of(updatedPojo));

        activationStore.refreshAllAsync().join();

        assertSame(firstInstance, activationStore.getAll().getFirst());
        assertEquals(LocalTime.MIDNIGHT, firstInstance.getActivationTime());
    }

    @Test
    void invalidateCacheAsync_ClearsData() {
        activationStore.invalidateCacheAsync().join();
        assertTrue(activationStore.getAll().isEmpty());
    }
}
