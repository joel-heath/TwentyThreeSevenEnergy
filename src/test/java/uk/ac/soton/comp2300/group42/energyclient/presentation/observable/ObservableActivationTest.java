package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservableActivationTest {
    final LocalDate christmas = LocalDate.of(2025, 12, 25);
    final LocalDate easter = LocalDate.of(2026, 4, 5);

    final LocalTime morning = LocalTime.of(8, 45);
    final LocalTime afternoon = LocalTime.of(13, 30);

    ActivationDTO nonRecurringDto;
    ActivationDTO recurringDto;

    ObservableActivation nonRecurring;
    ObservableActivation recurring;

    final Long dishwasherId = 2L;
    final Long washingMachineId = 1L;

    @Mock
    ObservableAppliance dishwasher;
    @Mock
    ObservableAppliance washingMachine;

    @BeforeEach void setUp() {
        nonRecurringDto = new ActivationDTO(dishwasherId, morning, christmas);
        recurringDto = new ActivationDTO(washingMachineId, afternoon,
                true,
                false,
                true,
                false,
                true,
                false,
                true
        );
        nonRecurring = new ObservableActivation(nonRecurringDto, dishwasher);
        recurring = new ObservableActivation(recurringDto, washingMachine);
    }

    @Test void testGetters_nonRecurring() {
        when(dishwasher.getId()).thenReturn(dishwasherId);

        assertEquals(nonRecurringDto.getId(), nonRecurring.getId(), "ID should match DTO");
        assertEquals(nonRecurringDto.getApplianceId(), nonRecurring.getAppliance().getId(), "Appliance ID should match DTO");
        assertEquals(nonRecurringDto.getActivationTime(), nonRecurring.getActivationTime(), "Activation Time should match DTO");
        assertEquals(nonRecurringDto.getActivationDate(), nonRecurring.getActivationDate(), "Activation Date should match DTO");
        assertEquals(nonRecurringDto.isRecursMonday(), nonRecurring.isRecursMonday(), "Recurrence flags should match DTO");
        assertEquals(nonRecurringDto.isRecursTuesday(), nonRecurring.isRecursTuesday(), "Recurrence flags should match DTO");
        assertEquals(nonRecurringDto.isRecursWednesday(), nonRecurring.isRecursWednesday(), "Recurrence flags should match DTO");
        assertEquals(nonRecurringDto.isRecursThursday(), nonRecurring.isRecursThursday(), "Recurrence flags should match DTO");
        assertEquals(nonRecurringDto.isRecursFriday(), nonRecurring.isRecursFriday(), "Recurrence flags should match DTO");
        assertEquals(nonRecurringDto.isRecursSaturday(), nonRecurring.isRecursSaturday(), "Recurrence flags should match DTO");
        assertEquals(nonRecurringDto.isRecursSunday(), nonRecurring.isRecursSunday(), "Recurrence flags should match DTO");
        assertFalse(nonRecurring.isRecurring(), "Model should evaluate itself as non-recurring based on DTO data");
    }

    @Test void testGetters_recurring() {
        when(washingMachine.getId()).thenReturn(washingMachineId);

        assertEquals(recurringDto.getId(), recurring.getId(), "ID should match DTO");
        assertEquals(recurringDto.getApplianceId(), recurring.getAppliance().getId(), "Appliance ID should match DTO");
        assertEquals(recurringDto.getActivationTime(), recurring.getActivationTime(), "Activation Time should match DTO");
        assertEquals(nonRecurringDto.getActivationDate(), nonRecurring.getActivationDate(), "Activation Date should match DTO");
        assertEquals(recurringDto.isRecursMonday(), recurring.isRecursMonday(), "Recurrence flags should match DTO");
        assertEquals(recurringDto.isRecursTuesday(), recurring.isRecursTuesday(), "Recurrence flags should match DTO");
        assertEquals(recurringDto.isRecursWednesday(), recurring.isRecursWednesday(), "Recurrence flags should match DTO");
        assertEquals(recurringDto.isRecursThursday(), recurring.isRecursThursday(), "Recurrence flags should match DTO");
        assertEquals(recurringDto.isRecursFriday(), recurring.isRecursFriday(), "Recurrence flags should match DTO");
        assertEquals(recurringDto.isRecursSaturday(), recurring.isRecursSaturday(), "Recurrence flags should match DTO");
        assertEquals(recurringDto.isRecursSunday(), recurring.isRecursSunday(), "Recurrence flags should match DTO");
        assertTrue(recurring.isRecurring(), "Model should evaluate itself as recurring based on DTO data");
    }

    @Test void testSetters_nonRecurring() {
        nonRecurring.setAppliance(washingMachine);
        nonRecurring.setActivationTime(afternoon);
        nonRecurring.setActivationDate(easter);

        assertEquals(washingMachine, nonRecurring.getAppliance(), "Appliance should be updated without error");
        assertEquals(afternoon, nonRecurring.getActivationTime(), "Activation Time should be updated without error");
        assertEquals(easter, nonRecurring.getActivationDate(), "Activation Date should be updated without error");
        assertFalse(nonRecurring.isRecurring(), "Model should still evaluate itself as non-recurring after update");
    }

    @Test void testSetters_recurring() {
        recurring.setAppliance(dishwasher);
        recurring.setActivationTime(morning);
        recurring.setRecursMonday(false);
        recurring.setRecursTuesday(false);
        recurring.setRecursWednesday(false);
        recurring.setRecursThursday(true);
        recurring.setRecursFriday(true);
        recurring.setRecursSaturday(true);
        recurring.setRecursSunday(true);

        assertEquals(dishwasher, recurring.getAppliance(), "Appliance should be updated without error");
        assertEquals(morning, recurring.getActivationTime(), "Activation Time should be updated without error");
        assertFalse(recurring.isRecursMonday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursTuesday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursWednesday(), "Recurrence flags should be updated without error");
        assertTrue(recurring.isRecursThursday(), "Recurrence flags should be updated without error");
        assertTrue(recurring.isRecursFriday(), "Recurrence flags should be updated without error");
        assertTrue(recurring.isRecursSaturday(), "Recurrence flags should be updated without error");
        assertTrue(recurring.isRecursSunday(), "Recurrence flags should be updated without error");
        assertTrue(recurring.isRecurring(), "Model should still evaluate itself as recurring after update");
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

        assertNull(nonRecurring.getActivationDate(), "Activation Date should be updated without error");
        assertTrue(nonRecurring.isRecursMonday(), "Recurrence flags should be updated without error");
        assertTrue(nonRecurring.isRecursTuesday(), "Recurrence flags should be updated without error");
        assertFalse(nonRecurring.isRecursWednesday(), "Recurrence flags should be updated without error");
        assertFalse(nonRecurring.isRecursThursday(), "Recurrence flags should be updated without error");
        assertTrue(nonRecurring.isRecursFriday(), "Recurrence flags should be updated without error");
        assertTrue(nonRecurring.isRecursSaturday(), "Recurrence flags should be updated without error");
        assertTrue(nonRecurring.isRecursSunday(), "Recurrence flags should be updated without error");
        assertTrue(nonRecurring.isRecurring(), "Model should evaluate itself as recurring after update");
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

        assertEquals(easter, recurring.getActivationDate(), "Activation Date should be updated without error");
        assertFalse(recurring.isRecursMonday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursTuesday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursWednesday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursThursday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursFriday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursSaturday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecursSunday(), "Recurrence flags should be updated without error");
        assertFalse(recurring.isRecurring(), "Model should evaluate itself as non-recurring after update");
    }

    @Test void testProperties_nonRecurring() {
        assertNotNull(nonRecurring.applianceProperty(), "Appliance property should not be null");
        assertNotNull(nonRecurring.activationTimeProperty(), "Activation Time property should not be null");
        assertNotNull(nonRecurring.activationDateProperty(), "Activation Date property should not be null");
        assertNotNull(nonRecurring.recursMondayProperty(), "Recurs Monday property should not be null");
        assertNotNull(nonRecurring.recursTuesdayProperty(), "Recurs Tuesday property should not be null");
        assertNotNull(nonRecurring.recursWednesdayProperty(), "Recurs Wednesday property should not be null");
        assertNotNull(nonRecurring.recursThursdayProperty(), "Recurs Thursday property should not be null");
        assertNotNull(nonRecurring.recursFridayProperty(), "Recurs Friday property should not be null");
        assertNotNull(nonRecurring.recursSaturdayProperty(), "Recurs Saturday property should not be null");
        assertNotNull(nonRecurring.recursSundayProperty(), "Recurs Sunday property should not be null");
    }

    @Test void testProperties_recurring() {
        assertNotNull(recurring.applianceProperty(), "Appliance property should not be null");
        assertNotNull(recurring.activationTimeProperty(), "Activation Time property should not be null");
        assertNotNull(recurring.activationDateProperty(), "Activation Date property should not be null");
        assertNotNull(recurring.recursMondayProperty(), "Recurs Monday property should not be null");
        assertNotNull(recurring.recursTuesdayProperty(), "Recurs Tuesday property should not be null");
        assertNotNull(recurring.recursWednesdayProperty(), "Recurs Wednesday property should not be null");
        assertNotNull(recurring.recursThursdayProperty(), "Recurs Thursday property should not be null");
        assertNotNull(recurring.recursFridayProperty(), "Recurs Friday property should not be null");
        assertNotNull(recurring.recursSaturdayProperty(), "Recurs Saturday property should not be null");
        assertNotNull(recurring.recursSundayProperty(), "Recurs Sunday property should not be null");
    }

    @Test void testCommit_nonRecurring() {
        when(washingMachine.getId()).thenReturn(washingMachineId);

        nonRecurring.setAppliance(washingMachine);
        nonRecurring.setActivationTime(afternoon);
        nonRecurring.setActivationDate(easter);
        ActivationDTO result = nonRecurring.commit();

        assertEquals(nonRecurringDto.getId(), result.getId(), "DTO ID should be unchanged");
        assertEquals(washingMachine.getId(), result.getApplianceId(), "DTO Appliance ID should be updated");
        assertEquals(afternoon, result.getActivationTime(), "DTO Activation Time should be updated");
        assertEquals(easter, result.getActivationDate(), "DTO Activation Date should be updated");
    }

    @Test void testCommit_recurring() {
        when(dishwasher.getId()).thenReturn(dishwasherId);

        recurring.setAppliance(dishwasher);
        recurring.setActivationTime(morning);
        recurring.setRecursMonday(false);
        recurring.setRecursTuesday(false);
        recurring.setRecursWednesday(false);
        recurring.setRecursThursday(true);
        recurring.setRecursFriday(true);
        recurring.setRecursSaturday(true);
        recurring.setRecursSunday(true);
        ActivationDTO result = recurring.commit();

        assertEquals(recurring.getId(), result.getId(), "DTO ID should be unchanged");
        assertEquals(dishwasher.getId(), result.getApplianceId(), "DTO Appliance ID should be updated");
        assertEquals(morning, result.getActivationTime(), "DTO Activation Time should be updated");
        assertFalse(result.isRecursMonday(), "DTO recurrence flags should be updated");
        assertFalse(result.isRecursTuesday(), "DTO recurrence flags should be updated");
        assertFalse(result.isRecursWednesday(), "DTO recurrence flags should be updated");
        assertTrue(result.isRecursThursday(), "DTO recurrence flags should be updated");
        assertTrue(result.isRecursFriday(), "DTO recurrence flags should be updated");
        assertTrue(result.isRecursSaturday(), "DTO recurrence flags should be updated");
        assertTrue(result.isRecursSunday(), "DTO recurrence flags should be updated");
    }

    @Test void testUpdateFrom_nonRecurring() {
        ActivationDTO newDto = new ActivationDTO(washingMachineId, afternoon, easter);

        nonRecurring.updateFrom(newDto, washingMachine);

        assertEquals(nonRecurringDto.getId(), nonRecurring.getId(), "Model ID should not have changed");
        assertEquals(washingMachine, nonRecurring.getAppliance(), "Model Appliance should be updated from new DTO");
        assertEquals(afternoon, nonRecurring.getActivationTime(), "Model Activation Time should be updated from new DTO");
        assertEquals(easter, nonRecurring.getActivationDate(), "Model Activation Date should be updated from new DTO");
    }

    @Test void testUpdateFrom_recurring() {
        ActivationDTO newDto = new ActivationDTO(dishwasherId, morning, false, false, false, true, true, true, true);

        recurring.updateFrom(newDto, dishwasher);

        assertEquals(recurringDto.getId(), recurring.getId(), "Model ID should not have changed");
        assertEquals(dishwasher, recurring.getAppliance(), "Model Appliance should be updated from new DTO");
        assertEquals(morning, recurring.getActivationTime(), "Model Activation Time should be updated from new DTO");
        assertFalse(recurring.isRecursMonday(), "Model recurrence flags should be updated from new DTO");
        assertFalse(recurring.isRecursTuesday(), "Model recurrence flags should be updated from new DTO");
        assertFalse(recurring.isRecursWednesday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(recurring.isRecursThursday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(recurring.isRecursFriday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(recurring.isRecursSaturday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(recurring.isRecursSunday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(recurring.isRecurring(), "Model should still evaluate itself as recurring");
    }

    @Test void testUpdateFrom_nonRecurring_to_recurring() {
        ActivationDTO newDto = new ActivationDTO(dishwasherId, morning, true, true, false, false, true, true, true);

        nonRecurring.updateFrom(newDto, dishwasher);

        assertNull(nonRecurring.getActivationDate(), "Model Activation Date should be updated from new DTO");
        assertTrue(nonRecurring.isRecursMonday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(nonRecurring.isRecursTuesday(), "Model recurrence flags should be updated from new DTO");
        assertFalse(nonRecurring.isRecursWednesday(), "Model recurrence flags should be updated from new DTO");
        assertFalse(nonRecurring.isRecursThursday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(nonRecurring.isRecursFriday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(nonRecurring.isRecursSaturday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(nonRecurring.isRecursSunday(), "Model recurrence flags should be updated from new DTO");
        assertTrue(nonRecurring.isRecurring(), "Model should evaluate itself as recurring after update");
    }

    @Test void testUpdateFrom_recurring_to_nonRecurring() {
        ActivationDTO newDto = new ActivationDTO(washingMachineId, afternoon, easter);

        recurring.updateFrom(newDto, washingMachine);

//        assertEquals(easter, recurring.getActivationDate(), "Model Activation Date should be updated from new DTO");
//        assertFalse(recurring.isRecursMonday(), "Model recurrence flags should be updated from new DTO");
//        assertFalse(recurring.isRecursTuesday(), "Model recurrence flags should be updated from new DTO");
//        assertFalse(recurring.isRecursWednesday(), "Model recurrence flags should be updated from new DTO");
//        assertFalse(recurring.isRecursThursday(), "Model recurrence flags should be updated from new DTO");
//        assertFalse(recurring.isRecursFriday(), "Model recurrence flags should be updated from new DTO");
//        assertFalse(recurring.isRecursSaturday(), "Model recurrence flags should be updated from new DTO");
//        assertFalse(recurring.isRecursSunday(), "Model recurrence flags should be updated from new DTO");
//        assertFalse(recurring.isRecurring(), "Model should evaluate itself as non-recurring after update");
    }

    @Test void testGetNextActivationDateTime_nonRecurring() {
        LocalDateTime expected = LocalDateTime.of(christmas, morning);
        assertEquals(expected, nonRecurring.getNextActivationDateTime());
    }
    @Test void testGetNextActivationDateTime_recurring() {
        // *The recurring activation is set for Monday, Wednesday, Friday and Sunday at 13:30.*
        LocalDateTime beforeActivation = easter.atTime(7, 0);
        LocalDateTime afterActivation = easter.atTime(14, 0);

        // The next activation after Easter Sunday is Easter Monday, April 6th at 13:30.
        LocalDateTime sunday = recurring.getNextActivationDateTime(beforeActivation);
        LocalDateTime monday = recurring.getNextActivationDateTime(afterActivation);

        // If we disable recurrence on Monday, the next activation is Wednesday, April 8th at 13:30.
        recurring.setRecursMonday(false);
        LocalDateTime wednesday = recurring.getNextActivationDateTime(afterActivation);

        assertEquals(easter.atTime(afternoon), sunday);
        assertEquals(easter.plusDays(1).atTime(afternoon), monday);
        assertEquals(easter.plusDays(3).atTime(afternoon), wednesday);
    }

    @Test void testRecursOnDay_nonRecurring() {
        assertEquals(nonRecurring.isRecursMonday(), nonRecurring.recursOnDay(DayOfWeek.MONDAY), "Recurs on day should match getter");
        assertEquals(nonRecurring.isRecursTuesday(), nonRecurring.recursOnDay(DayOfWeek.TUESDAY), "Recurs on day should match getter");
        assertEquals(nonRecurring.isRecursWednesday(), nonRecurring.recursOnDay(DayOfWeek.WEDNESDAY), "Recurs on day should match getter");
        assertEquals(nonRecurring.isRecursThursday(), nonRecurring.recursOnDay(DayOfWeek.THURSDAY), "Recurs on day should match getter");
        assertEquals(nonRecurring.isRecursFriday(), nonRecurring.recursOnDay(DayOfWeek.FRIDAY), "Recurs on day should match getter");
        assertEquals(nonRecurring.isRecursSaturday(), nonRecurring.recursOnDay(DayOfWeek.SATURDAY), "Recurs on day should match getter");
        assertEquals(nonRecurring.isRecursSunday(), nonRecurring.recursOnDay(DayOfWeek.SUNDAY), "Recurs on day should match getter");
    }

    @Test void testRecursOnDay_recurring() {
        assertEquals(recurring.isRecursMonday(), recurring.recursOnDay(DayOfWeek.MONDAY), "Recurs on day should match getter");
        assertEquals(recurring.isRecursTuesday(), recurring.recursOnDay(DayOfWeek.TUESDAY), "Recurs on day should match getter");
        assertEquals(recurring.isRecursWednesday(), recurring.recursOnDay(DayOfWeek.WEDNESDAY), "Recurs on day should match getter");
        assertEquals(recurring.isRecursThursday(), recurring.recursOnDay(DayOfWeek.THURSDAY), "Recurs on day should match getter");
        assertEquals(recurring.isRecursFriday(), recurring.recursOnDay(DayOfWeek.FRIDAY), "Recurs on day should match getter");
        assertEquals(recurring.isRecursSaturday(), recurring.recursOnDay(DayOfWeek.SATURDAY), "Recurs on day should match getter");
        assertEquals(recurring.isRecursSunday(), recurring.recursOnDay(DayOfWeek.SUNDAY), "Recurs on day should match getter");
    }

}
