package uk.ac.soton.comp2300.group42.energyclient.data.local;

import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.*;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LocalStorageDataTest {

    @Test
    void updateFrom_SuccessfullyCopiesAllFields() {
        LocalStorageData source = new LocalStorageData();
        source.user = new User(1L, "John Doe", "john.doe@example.com");
        source.housemate = new Housemate(1L, 1L, "Jane Doe", "jane.doe@example.com", Role.OWNER);
        source.preferences = new Preferences(1L, true, ColorVision.PROTAN, Theme.DARK, Mode.ADVANCED, true, 1.5, 1L);

        source.appliances.put(99L, mock(Appliance.class));
        source.activations.put(88L, mock(Activation.class));
        source.houses.put(77L, mock(House.class));
        source.metrics.put(66L, mock(Metric.class));

        LocalStorageData target = new LocalStorageData();

        target.updateFrom(source);

        assertEquals(source.user, target.user);
        assertEquals(source.housemate, target.housemate);
        assertEquals(source.preferences, target.preferences);
        assertSame(source.appliances, target.appliances);
        assertSame(source.activations, target.activations);
        assertSame(source.houses, target.houses);
        assertSame(source.metrics, target.metrics);
    }

    @Test
    void nextApplianceId_GeneratesUniqueId() {
        LocalStorageData data = new LocalStorageData();

        data.appliances.put(5L, null);
        data.appliances.put(1024L, null);
        data.appliances.put(42L, null);

        Long generatedId = data.nextApplianceId();

        assertTrue(generatedId > 0);
        assertFalse(data.appliances.containsKey(generatedId), "Generated ID " + generatedId + " already exists in the map!");
    }

    @Test
    void nextApplianceId_WhenMapIsEmpty_GeneratesValidUniqueId() {
        LocalStorageData data = new LocalStorageData();

        Long generatedId = data.nextApplianceId();

        assertNotNull(generatedId);
        assertTrue(generatedId > 0);

        assertFalse(data.appliances.containsKey(generatedId));
    }
}