package uk.ac.soton.comp2300.group42.energyclient.presentation.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.ZoneId;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplianceStoreTest {

    @Mock private ApplianceRepository repository;
    @Mock private HouseStore houseStore;

    private ApplianceStore applianceStore;

    @BeforeEach
    void setUp() {
        Executor directExecutor = Runnable::run;
        ObservableHouse house = new ObservableHouse(new House(100L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        ObservablePreferences preferences = new ObservablePreferences(new Preferences(), house);

        when(houseStore.get(100L)).thenReturn(house);

        applianceStore = new ApplianceStore(repository, houseStore, preferences, directExecutor);
    }

    @Test
    void add_addsApplianceToMasterList() {
        Appliance created = new Appliance(1L, 100L, "Kettle");
        when(repository.add(created)).thenReturn(created);

        var result = applianceStore.add(created);

        assertEquals(1L, result.getId());
        assertEquals("Kettle", result.getName());
        assertTrue(applianceStore.getAll().contains(result));
        verify(repository).add(created);
    }

    @Test
    void delete_usesActiveHouseIdAndRemovesItem() {
        Appliance appliance = new Appliance(2L, 100L, "Toaster");
        when(repository.get(100L, 2L)).thenReturn(appliance);
        applianceStore.get(2L);

        applianceStore.delete(2L);

        assertTrue(applianceStore.getAll().isEmpty());
        verify(repository).delete(100L, 2L);
    }
}
