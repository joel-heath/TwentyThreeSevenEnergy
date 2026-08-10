package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.activation.ActivationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActivationTypeConverterTest {

    private ActivationTypeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ActivationTypeConverter();
    }

    @Test
    void convertToDatabaseColumn_recurring_returnsRecurringId() {
        assertEquals("recurring", converter.convertToDatabaseColumn(ActivationType.RECURRING));
    }

    @Test
    void convertToDatabaseColumn_nonRecurring_returnsNonRecurringId() {
        assertEquals("non_recurring", converter.convertToDatabaseColumn(ActivationType.NON_RECURRING));
    }

    @Test
    void convertToDatabaseColumn_null_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_recurringId_returnsRecurringEnum() {
        assertEquals(ActivationType.RECURRING, converter.convertToEntityAttribute("recurring"));
    }

    @Test
    void convertToEntityAttribute_nonRecurringId_returnsNonRecurringEnum() {
        assertEquals(ActivationType.NON_RECURRING, converter.convertToEntityAttribute("non_recurring"));
    }

    @Test
    void convertToEntityAttribute_null_returnsNull() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttribute_unknownId_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("invalid_id")
        );
        assertEquals("Unknown activation type id: invalid_id", exception.getMessage());
    }

    @Test
    void convertToEntityAttribute_emptyString_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("")
        );
        assertEquals("Unknown activation type id: ", exception.getMessage());
    }
}