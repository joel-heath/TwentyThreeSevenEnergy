package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ObservableHouseTest {

    House domain;
    ObservableHouse presentation;

    @BeforeEach void setUp() {
        domain = new House(
                10L,
                "Primary House",
                "123 Main St",
                ZoneId.of("Europe/London"),
                Role.RESIDENT
        );

        presentation = new ObservableHouse(domain);
    }

    @Test void testGetters() {
        assertEquals(domain.id(), presentation.getId(), "ID should match domain model");
        assertEquals(domain.name(), presentation.getName(), "Name should match domain model");
        assertEquals(domain.address(), presentation.getAddress(), "Address should match domain model");
        assertEquals(domain.timezone(), presentation.getTimezone(), "Timezone should match domain model");
        assertEquals(domain.role(), presentation.getRole(), "Role should match domain model");
    }

    @Test void testSetters() {
        presentation.setName("Secondary House");
        presentation.setAddress("456 Park Ave");
        presentation.setTimezone(ZoneId.of("America/New_York"));
        presentation.setRole(Role.OWNER);

        assertEquals("Secondary House", presentation.getName(), "Name should be updated after setter");
        assertEquals("456 Park Ave", presentation.getAddress(), "Address should be updated after setter");
        assertEquals(ZoneId.of("America/New_York"), presentation.getTimezone(), "Timezone should be updated after setter");
        assertEquals(Role.OWNER, presentation.getRole(), "Role should be updated after setter");
    }

    @Test void testProperties() {
        assertNotNull(presentation.nameProperty(), "Name property should not be null");
        assertNotNull(presentation.addressProperty(), "Address property should not be null");
        assertNotNull(presentation.timezoneProperty(), "Timezone property should not be null");
        assertNotNull(presentation.roleProperty(), "Role property should not be null");
    }

    @Test void testCommit() {
        presentation.setName("Secondary House");
        presentation.setAddress("456 Park Ave");
        presentation.setTimezone(ZoneId.of("America/New_York"));
        presentation.setRole(Role.OWNER);

        House result = presentation.commit();

        assertEquals(10L, result.id(), "Domain model ID should be correct");
        assertEquals("Secondary House", result.name(), "Domain model Name should be correct");
        assertEquals("456 Park Ave", result.address(), "Domain model Address should be correct");
        assertEquals(ZoneId.of("America/New_York"), result.timezone(), "Domain model Timezone should be correct");
        assertEquals(Role.OWNER, result.role(), "Domain model Role should be correct");
    }

    @Test void testUpdateFrom() {
        House newDomain = new House(
            20L,
            "Secondary House",
            "456 Park Ave",
            ZoneId.of("America/New_York"),
            Role.OWNER
        );

        presentation.updateFrom(newDomain);

        assertEquals(10L, presentation.getId(), "ID should not have changed");
        assertEquals("Secondary House", presentation.getName(), "Name should be updated from domain model");
        assertEquals("456 Park Ave", presentation.getAddress(), "Address should be updated from domain model");
        assertEquals(ZoneId.of("America/New_York"), presentation.getTimezone(), "Timezone should be updated from domain model");
        assertEquals(Role.OWNER, presentation.getRole(), "Role should be updated from domain model");
    }
}
