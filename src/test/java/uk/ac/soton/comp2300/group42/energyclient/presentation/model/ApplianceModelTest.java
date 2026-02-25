package uk.ac.soton.comp2300.group42.energyclient.presentation.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;

import static org.junit.jupiter.api.Assertions.*;

public class ApplianceModelTest {
    ApplianceDTO dto;
    ApplianceModel model;

    @BeforeEach void setUp() {
        dto = new ApplianceDTO(1L, "Washing Machine");
        model = new ApplianceModel(dto);
    }

    @Test void testGetters() {
        assertEquals(dto.getId(), model.getId(), "ID should match DTO");
        assertEquals(dto.getName(), model.getName(), "Appliance Name should match DTO");
    }

    @Test void testSetters() {
        model.setName("Dishwasher");
        assertEquals("Dishwasher", model.getName());
    }

    @Test void testProperties() {
        assertNotNull(model.nameProperty());
    }

    @Test void testCommit() {
        model.setName("Oven");

        ApplianceDTO result = model.commit();

        assertEquals(1L, result.getId(), "DTO ID should be unchanged");
        assertEquals("Oven", result.getName(), "DTO Name should be updated");
    }

    @Test void testUpdateFrom() {
        ApplianceDTO newDto = new ApplianceDTO(4L, "Bedroom 1 Lights");

        model.updateFrom(newDto); // should it throw an exception on incorrect id?

        assertEquals(1L, model.getId(), "Model ID should not have changed");
        assertEquals("Bedroom 1 Lights", model.getName(), "Model Name should be updated from new DTO");
    }

    @Test void testToString() {
        assertTrue(model.toString().contains(dto.getName()));
    }
}
