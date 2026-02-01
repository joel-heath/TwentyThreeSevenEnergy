package uk.ac.soton.comp2300.group42.energyclient.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplianceTest {
    @Test void testGetters() {
        Appliance app = new Appliance(1, "Dishwasher");

        assertEquals(1, app.getId());
        assertEquals("Dishwasher", app.getName());
    }

    @Test void testSetters() {
        Appliance app = new Appliance(1, "Dishwasher");
        app.setName("Washing Machine");

        assertEquals("Washing Machine", app.getName());
    }

    @Test void testToString() {
        Appliance app = new Appliance(1, "Washing Machine");
        assertTrue(app.toString().contains("Washing Machine"));
    }
}
