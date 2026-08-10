package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThemeConverterTest {

    private ThemeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ThemeConverter();
    }

    // --- convertToDatabaseColumn Tests ---

    @Test
    void convertToDatabaseColumn_light_returnsLightId() {
        assertEquals("light", converter.convertToDatabaseColumn(Theme.LIGHT));
    }

    @Test
    void convertToDatabaseColumn_dark_returnsDarkId() {
        assertEquals("dark", converter.convertToDatabaseColumn(Theme.DARK));
    }

    @Test
    void convertToDatabaseColumn_lightContrast_returnsLightContrastId() {
        assertEquals("light_high_contrast", converter.convertToDatabaseColumn(Theme.LIGHT_CONTRAST));
    }

    @Test
    void convertToDatabaseColumn_darkContrast_returnsDarkContrastId() {
        assertEquals("dark_high_contrast", converter.convertToDatabaseColumn(Theme.DARK_CONTRAST));
    }

    @Test
    void convertToDatabaseColumn_null_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    // --- convertToEntityAttribute Tests ---

    @Test
    void convertToEntityAttribute_lightId_returnsLightEnum() {
        assertEquals(Theme.LIGHT, converter.convertToEntityAttribute("light"));
    }

    @Test
    void convertToEntityAttribute_darkId_returnsDarkEnum() {
        assertEquals(Theme.DARK, converter.convertToEntityAttribute("dark"));
    }

    @Test
    void convertToEntityAttribute_lightContrastId_returnsLightContrastEnum() {
        assertEquals(Theme.LIGHT_CONTRAST, converter.convertToEntityAttribute("light_high_contrast"));
    }

    @Test
    void convertToEntityAttribute_darkContrastId_returnsDarkContrastEnum() {
        assertEquals(Theme.DARK_CONTRAST, converter.convertToEntityAttribute("dark_high_contrast"));
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
        assertEquals("Unknown theme id: invalid_id", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_emptyString_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("")
        );
        assertEquals("Unknown theme id: ", exception.getMessage());
    }
}