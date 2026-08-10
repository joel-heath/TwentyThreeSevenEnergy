package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.preferences.Mode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModeConverterTest {

    private ModeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ModeConverter();
    }

    // --- convertToDatabaseColumn Tests ---

    @Test
    void convertToDatabaseColumn_simple_returnsSimpleId() {
        assertEquals("simple", converter.convertToDatabaseColumn(Mode.SIMPLE));
    }

    @Test
    void convertToDatabaseColumn_advanced_returnsAdvancedId() {
        assertEquals("advanced", converter.convertToDatabaseColumn(Mode.ADVANCED));
    }

    @Test
    void convertToDatabaseColumn_null_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    // --- convertToEntityAttribute Tests ---

    @Test
    void convertToEntityAttribute_simpleId_returnsSimpleEnum() {
        assertEquals(Mode.SIMPLE, converter.convertToEntityAttribute("simple"));
    }

    @Test
    void convertToEntityAttribute_advancedId_returnsAdvancedEnum() {
        assertEquals(Mode.ADVANCED, converter.convertToEntityAttribute("advanced"));
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
        assertEquals("Unknown mode id: invalid_id", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_emptyString_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("")
        );
        assertEquals("Unknown mode id: ", exception.getMessage());
    }
}