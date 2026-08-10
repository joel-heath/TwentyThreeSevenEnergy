package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorVisionConverterTest {

    private ColorVisionConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ColorVisionConverter();
    }

    // --- convertToDatabaseColumn Tests ---

    @Test
    void convertToDatabaseColumn_typical_returnsTypicalId() {
        assertEquals("typical", converter.convertToDatabaseColumn(ColorVision.TYPICAL));
    }

    @Test
    void convertToDatabaseColumn_protan_returnsProtanopiaId() {
        assertEquals("protanopia", converter.convertToDatabaseColumn(ColorVision.PROTAN));
    }

    @Test
    void convertToDatabaseColumn_deuteran_returnsDeuteranopiaId() {
        assertEquals("deuteranopia", converter.convertToDatabaseColumn(ColorVision.DEUTERAN));
    }

    @Test
    void convertToDatabaseColumn_tritan_returnsTritanopiaId() {
        assertEquals("tritanopia", converter.convertToDatabaseColumn(ColorVision.TRITAN));
    }

    @Test
    void convertToDatabaseColumn_achroma_returnsAchromatopsiaId() {
        assertEquals("achromatopsia", converter.convertToDatabaseColumn(ColorVision.ACHROMA));
    }

    @Test
    void convertToDatabaseColumn_null_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    // --- convertToEntityAttribute Tests ---

    @Test
    void convertToEntityAttribute_typicalId_returnsTypicalEnum() {
        assertEquals(ColorVision.TYPICAL, converter.convertToEntityAttribute("typical"));
    }

    @Test
    void convertToEntityAttribute_protanopiaId_returnsProtanEnum() {
        assertEquals(ColorVision.PROTAN, converter.convertToEntityAttribute("protanopia"));
    }

    @Test
    void convertToEntityAttribute_deuteranopiaId_returnsDeuteranEnum() {
        assertEquals(ColorVision.DEUTERAN, converter.convertToEntityAttribute("deuteranopia"));
    }

    @Test
    void convertToEntityAttribute_tritanopiaId_returnsTritanEnum() {
        assertEquals(ColorVision.TRITAN, converter.convertToEntityAttribute("tritanopia"));
    }

    @Test
    void convertToEntityAttribute_achromatopsiaId_returnsAchromaEnum() {
        assertEquals(ColorVision.ACHROMA, converter.convertToEntityAttribute("achromatopsia"));
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
        assertEquals("Unknown color vision id: invalid_id", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_emptyString_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("")
        );
        assertEquals("Unknown color vision id: ", exception.getMessage());
    }
}