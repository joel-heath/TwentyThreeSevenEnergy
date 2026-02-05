package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplianceDTOTest {
    @Test void testGetters() {
        ApplianceDTO app = new ApplianceDTO(1, "Dishwasher");

        assertEquals(1, app.getId());
        assertEquals("Dishwasher", app.getName());
    }

    @Test void testSetters() {
        ApplianceDTO app = new ApplianceDTO(1, "Dishwasher");
        app.setName("Washing Machine");

        assertEquals("Washing Machine", app.getName());
    }

    @Test void testToString() {
        ApplianceDTO app = new ApplianceDTO(1, "Washing Machine");
        assertTrue(app.toString().contains("Washing Machine"));
    }
}
