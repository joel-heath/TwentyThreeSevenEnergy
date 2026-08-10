package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservableApplianceTest {

    Appliance domain;
    ObservableAppliance presentation;

    @Mock ObservableHouse mockHouse1;
    @Mock ObservableHouse mockHouse2;

    @BeforeEach void setUp() {
        domain = new Appliance(10L, 1L, "Washing Machine");
        presentation = new ObservableAppliance(domain, mockHouse1);
    }

    @Test void testGetters() {
        when(mockHouse1.getId()).thenReturn(1L);

        assertEquals(domain.id(), presentation.getId(), "ID should match domain model");
        assertEquals(domain.houseId(), presentation.getHouse().getId(), "House ID should match domain model");
        assertEquals(domain.name(), presentation.getName(), "Appliance Name should match domain model");
        assertEquals(mockHouse1, presentation.getHouse(), "House should match House given in constructor");
    }

    @Test void testSetters() {
        presentation.setName("Dishwasher");

        assertEquals("Dishwasher", presentation.getName(), "Name should be updated after setter");
    }

    @Test void testProperties() {
        assertNotNull(presentation.nameProperty(), "Name property should not be null");
    }

    @Test void testCommit() {
        when(mockHouse2.getId()).thenReturn(2L);

        presentation.setHouse(mockHouse2);
        presentation.setName("Oven");

        Appliance result = presentation.commit();

        assertEquals(10L, result.id(), "Domain model ID should be correct");
        assertEquals(mockHouse2.getId(), result.houseId(), "Domain model House ID should be correct");
        assertEquals("Oven", result.name(), "Domain model Name should be correct");
    }

    @Test void testUpdateFrom() {
        Appliance newDomain = new Appliance(20L, 2L,"Bedroom 1 Lights");

        presentation.updateFrom(newDomain, mockHouse2);

        assertEquals(10L, presentation.getId(), "ID should not have changed");
        assertEquals(mockHouse2, presentation.getHouse(), "House should be updated");
        assertEquals("Bedroom 1 Lights", presentation.getName(), "Name should be updated from domain model");
    }

    @Test void testToString() {
        assertTrue(presentation.toString().contains(domain.name()));
    }
}
