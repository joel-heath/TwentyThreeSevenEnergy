package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservableHousemateTest {

    Housemate domain;
    ObservableHousemate presentation;

    @Mock ObservableHouse mockHouse1;
    @Mock ObservableHouse mockHouse2;

    @BeforeEach void setUp() {
        domain = new Housemate(
                10L,
                1L,
                "Alice",
                "alice@example.com",
                Role.GUEST
        );

        presentation = new ObservableHousemate(domain, mockHouse1);
    }

    @Test void testGetters() {
        when(mockHouse1.getId()).thenReturn(1L);

        assertEquals(domain.userId(), presentation.getId(), "ID should match domain model");
        assertEquals(domain.houseId(), presentation.getHouse().getId(), "House ID should match domain model");
        assertEquals(domain.name(), presentation.getName(), "Name should match domain model");
        assertEquals(domain.email(), presentation.getEmail(), "Email should match domain model");
        assertEquals(domain.role(), presentation.getRole(), "Role should match domain model");
        assertEquals(mockHouse1, presentation.getHouse(), "House should match House given in constructor");
    }

    @Test void testSetters() {
        presentation.setName("Alice Smith");
        presentation.setEmail("a.smith@example.com");
        presentation.setRole(Role.OWNER);
        presentation.setHouse(mockHouse2);

        assertEquals("Alice Smith", presentation.getName(), "Name should be updated after setter");
        assertEquals("a.smith@example.com", presentation.getEmail(), "Email should be updated after setter");
        assertEquals(Role.OWNER, presentation.getRole(), "Role should be updated after setter");
        assertEquals(mockHouse2, presentation.getHouse(), "House should be updated after setter");
    }

    @Test void testProperties() {
        assertNotNull(presentation.nameProperty(), "Name property should not be null");
        assertNotNull(presentation.emailProperty(), "Email property should not be null");
        assertNotNull(presentation.roleProperty(), "Role property should not be null");
        assertNotNull(presentation.houseProperty(), "House property should not be null");
    }

    @Test void testCommit() {
        when(mockHouse2.getId()).thenReturn(2L);

        presentation.setName("Alice Smith");
        presentation.setEmail("a.smith@example.com");
        presentation.setRole(Role.OWNER);
        presentation.setHouse(mockHouse2);

        Housemate result = presentation.commit();

        assertEquals(10L, result.userId(), "Domain model ID should be correct");
        assertEquals("Alice Smith", result.name(), "Domain model Name should be correct");
        assertEquals("a.smith@example.com", result.email(), "Domain model Email should be correct");
        assertEquals(Role.OWNER, result.role(), "Domain model Role should be correct");
        assertEquals(mockHouse2.getId(), result.houseId(), "Domain model House ID should be correct");
    }

    @Test void testUpdateFrom() {
        Housemate newDomain = new Housemate(
            20L,
            2L,
            "Alice Smith",
            "a.smith@example.com",
            Role.OWNER
        );

        presentation.updateFrom(newDomain, mockHouse2);

        assertEquals(20L, presentation.getId(), "ID should be updated from domain model");
        assertEquals("Alice Smith", presentation.getName(), "Name should be updated from domain model");
        assertEquals("a.smith@example.com", presentation.getEmail(), "Email should be updated from domain model");
        assertEquals(Role.OWNER, presentation.getRole(), "Role should be updated from domain model");
        assertEquals(mockHouse2, presentation.getHouse(), "Active House should be updated");
    }
}
