package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.common.Role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleConverterTest {

    private RoleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RoleConverter();
    }

    // --- convertToDatabaseColumn Tests ---

    @Test
    void convertToDatabaseColumn_owner_returnsOwnerId() {
        assertEquals("owner", converter.convertToDatabaseColumn(Role.OWNER));
    }

    @Test
    void convertToDatabaseColumn_resident_returnsResidentId() {
        assertEquals("resident", converter.convertToDatabaseColumn(Role.RESIDENT));
    }

    @Test
    void convertToDatabaseColumn_guest_returnsGuestId() {
        assertEquals("guest", converter.convertToDatabaseColumn(Role.GUEST));
    }

    @Test
    void convertToDatabaseColumn_null_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    // --- convertToEntityAttribute Tests ---

    @Test
    void convertToEntityAttribute_ownerId_returnsOwnerEnum() {
        assertEquals(Role.OWNER, converter.convertToEntityAttribute("owner"));
    }

    @Test
    void convertToEntityAttribute_residentId_returnsResidentEnum() {
        assertEquals(Role.RESIDENT, converter.convertToEntityAttribute("resident"));
    }

    @Test
    void convertToEntityAttribute_guestId_returnsGuestEnum() {
        assertEquals(Role.GUEST, converter.convertToEntityAttribute("guest"));
    }

    @Test
    void convertToEntityAttribute_null_returnsNull() {
        // The converter intercepts null before it reaches the enum's fromId method
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttribute_unknownId_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("invalid_id")
        );
        assertEquals("Unknown role id: invalid_id", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_emptyString_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("")
        );
        assertEquals("Unknown role id: ", exception.getMessage());
    }
}