package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ActivationDTOTest {
    final Long dishwasherId = 1L;
    final Long washingMachineId = 2L;

    final LocalDate christmas = LocalDate.of(2025, 12, 25);
    final LocalDate easter = LocalDate.of(2026, 4, 5);

    final LocalTime morning = LocalTime.of(8, 45);
    final LocalTime afternoon = LocalTime.of(13, 30);

    ActivationDTO nonRecurring;
    ActivationDTO recurring;

    @BeforeEach void setup() {
        nonRecurring = new ActivationDTO(dishwasherId, morning, christmas);
        recurring = new ActivationDTO(washingMachineId, afternoon,
                true,
                false,
                true,
                false,
                true,
                false,
                false
        );

    }

    @Test void testGetters_nonRecurring() {
        assertNull(nonRecurring.getId(), "ID should be null for new ActivationDTO");
        assertEquals(dishwasherId, nonRecurring.getApplianceId(), "Appliance ID should be set correctly");
        assertEquals(morning, nonRecurring.getActivationTime(), "Activation time should be set correctly");
        assertEquals(christmas, nonRecurring.getActivationDate(), "Activation date should be set for non-recurring activations");
        assertFalse(nonRecurring.isRecursMonday(), "Non-recurring activation should not have positive recurrence flags");
        assertFalse(nonRecurring.isRecursTuesday(), "Non-recurring activation should not have positive recurrence flags");
        assertFalse(nonRecurring.isRecursWednesday(), "Non-recurring activation should not have positive recurrence flags");
        assertFalse(nonRecurring.isRecursThursday(), "Non-recurring activation should not have positive recurrence flags");
        assertFalse(nonRecurring.isRecursFriday(), "Non-recurring activation should not have positive recurrence flags");
        assertFalse(nonRecurring.isRecursSaturday(), "Non-recurring activation should not have positive recurrence flags");
        assertFalse(nonRecurring.isRecursSunday(), "Non-recurring activation should not have positive recurrence flags");
    }

    @Test void testGetters_recurring() {
        assertNull(recurring.getId(), "ID should be null for new ActivationDTO");
        assertEquals(washingMachineId, recurring.getApplianceId(), "Appliance ID should be set correctly");
        assertEquals(afternoon, recurring.getActivationTime(), "Activation time should be set correctly");
        assertNull(recurring.getActivationDate(), "Recurring activation should not have activation date set");
        assertTrue(recurring.isRecursMonday(), "Recurring activation should have correctly set recurrence flags");
        assertFalse(recurring.isRecursTuesday(), "Recurring activation should have correctly set recurrence flags");
        assertTrue(recurring.isRecursWednesday(), "Recurring activation should have correctly set recurrence flags");
        assertFalse(recurring.isRecursThursday(), "Recurring activation should have correctly set recurrence flags");
        assertTrue(recurring.isRecursFriday(), "Recurring activation should have correctly set recurrence flags");
        assertFalse(recurring.isRecursSaturday(), "Recurring activation should have correctly set recurrence flags");
        assertFalse(recurring.isRecursSunday(), "Recurring activation should have correctly set recurrence flags");
    }

    @Test void testSetters_nonRecurring() {
        nonRecurring.setApplianceId(washingMachineId);
        nonRecurring.setActivationTime(afternoon);
        nonRecurring.setActivationDate(easter);

        assertEquals(washingMachineId, nonRecurring.getApplianceId());
        assertEquals(afternoon, nonRecurring.getActivationTime());
        assertEquals(easter, nonRecurring.getActivationDate());
    }

    @Test void testSetters_recurring() {
        recurring.setApplianceId(dishwasherId);
        recurring.setActivationTime(morning);
        recurring.setRecursMonday(false);
        recurring.setRecursTuesday(false);
        recurring.setRecursWednesday(false);
        recurring.setRecursThursday(true);
        recurring.setRecursFriday(true);
        recurring.setRecursSaturday(true);
        recurring.setRecursSunday(true);

        assertEquals(dishwasherId, recurring.getApplianceId());
        assertEquals(morning, recurring.getActivationTime());
        assertFalse(recurring.isRecursMonday());
        assertFalse(recurring.isRecursTuesday());
        assertFalse(recurring.isRecursWednesday());
        assertTrue(recurring.isRecursThursday());
        assertTrue(recurring.isRecursFriday());
        assertTrue(recurring.isRecursSaturday());
        assertTrue(recurring.isRecursSunday());
    }

    @Test void testSetters_nonRecurring_to_recurring() {
        nonRecurring.setActivationDate(null);
        nonRecurring.setRecursMonday(true);
        nonRecurring.setRecursTuesday(true);
        nonRecurring.setRecursWednesday(false);
        nonRecurring.setRecursThursday(false);
        nonRecurring.setRecursFriday(true);
        nonRecurring.setRecursSaturday(true);
        nonRecurring.setRecursSunday(true);

        assertNull(nonRecurring.getActivationDate());
        assertTrue(nonRecurring.isRecursMonday());
        assertTrue(nonRecurring.isRecursTuesday());
        assertFalse(nonRecurring.isRecursWednesday());
        assertFalse(nonRecurring.isRecursThursday());
        assertTrue(nonRecurring.isRecursFriday());
        assertTrue(nonRecurring.isRecursSaturday());
        assertTrue(nonRecurring.isRecursSunday());
    }

    @Test void testSetters_recurring_to_nonRecurring() {
        recurring.setActivationDate(easter);
        recurring.setRecursMonday(false);
        recurring.setRecursTuesday(false);
        recurring.setRecursWednesday(false);
        recurring.setRecursThursday(false);
        recurring.setRecursFriday(false);
        recurring.setRecursSaturday(false);
        recurring.setRecursSunday(false);

        assertEquals(easter, recurring.getActivationDate());
        assertFalse(recurring.isRecursMonday());
        assertFalse(recurring.isRecursTuesday());
        assertFalse(recurring.isRecursWednesday());
        assertFalse(recurring.isRecursThursday());
        assertFalse(recurring.isRecursFriday());
        assertFalse(recurring.isRecursSaturday());
        assertFalse(recurring.isRecursSunday());
    }
}
