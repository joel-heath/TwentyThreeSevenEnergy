package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;

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

    final Long dishwasherId = 1L;
    final Long washingMachineId = 2L;
    final Long houseId = 10L;

    @Mock ObservableAppliance dishwasher;
    @Mock ObservableAppliance washingMachine;
    @Mock ObservableHouse house;

    Activation nonRecurringDomain;
    Activation recurringDomain;

    ObservableActivation nonRecurring;
    ObservableActivation recurring;

    @BeforeEach void setUp() {
        nonRecurringDomain = new Activation(dishwasherId, houseId, morning, christmas);
        recurringDomain = new Activation(washingMachineId, houseId, afternoon,
                true,
                false,
                true,
                false,
                true,
                false,
                true
        );
        nonRecurring = new ObservableActivation(nonRecurringDomain, dishwasher);
        recurring = new ObservableActivation(recurringDomain, washingMachine);
    }

    @Test void testGetters_nonRecurring() {
        when(dishwasher.getId()).thenReturn(dishwasherId);
        when(dishwasher.getHouse()).thenReturn(house);
        when(house.getId()).thenReturn(houseId);

        assertEquals(nonRecurringDomain.id(), nonRecurring.getId(), "ID should match domain model");
        assertEquals(nonRecurringDomain.applianceId(), nonRecurring.getAppliance().getId(), "Appliance ID should match domain model");
        assertEquals(nonRecurringDomain.houseId(), nonRecurring.getAppliance().getHouse().getId(), "House ID should match domain model");
        assertEquals(nonRecurringDomain.type(), nonRecurring.getActivationType(), "Type should match domain model");
        assertEquals(nonRecurringDomain.activationTime(), nonRecurring.getActivationTime(), "Activation Time should match domain model");
        assertEquals(nonRecurringDomain.activationDate(), nonRecurring.getActivationDate(), "Activation Date should match domain model");
        assertEquals(nonRecurringDomain.recursMonday(), nonRecurring.isRecursMonday(), "Recurrence flags should match domain model");
        assertEquals(nonRecurringDomain.recursTuesday(), nonRecurring.isRecursTuesday(), "Recurrence flags should match domain model");
        assertEquals(nonRecurringDomain.recursWednesday(), nonRecurring.isRecursWednesday(), "Recurrence flags should match domain model");
        assertEquals(nonRecurringDomain.recursThursday(), nonRecurring.isRecursThursday(), "Recurrence flags should match domain model");
        assertEquals(nonRecurringDomain.recursFriday(), nonRecurring.isRecursFriday(), "Recurrence flags should match domain model");
        assertEquals(nonRecurringDomain.recursSaturday(), nonRecurring.isRecursSaturday(), "Recurrence flags should match domain model");
        assertEquals(nonRecurringDomain.recursSunday(), nonRecurring.isRecursSunday(), "Recurrence flags should match domain model");
        assertEquals(dishwasher, nonRecurring.getAppliance(), "Appliance should match Appliance given in constructor");
    }

    @Test void testGetters_recurring() {
        when(washingMachine.getId()).thenReturn(washingMachineId);
        when(washingMachine.getHouse()).thenReturn(house);
        when(house.getId()).thenReturn(houseId);

        assertEquals(recurringDomain.id(), recurring.getId(), "ID should match domain model");
        assertEquals(recurringDomain.applianceId(), recurring.getAppliance().getId(), "Appliance ID should match domain model");
        assertEquals(recurringDomain.houseId(), recurring.getAppliance().getHouse().getId(), "House ID should match domain model");
        assertEquals(recurringDomain.type(), recurring.getActivationType(), "Type should match domain model");
        assertEquals(recurringDomain.activationTime(), recurring.getActivationTime(), "Activation Time should match domain model");
        assertEquals(recurringDomain.activationDate(), recurring.getActivationDate(), "Activation Date should match domain model");
        assertEquals(recurringDomain.recursMonday(), recurring.isRecursMonday(), "Recurrence flags should match domain model");
        assertEquals(recurringDomain.recursTuesday(), recurring.isRecursTuesday(), "Recurrence flags should match domain model");
        assertEquals(recurringDomain.recursWednesday(), recurring.isRecursWednesday(), "Recurrence flags should match domain model");
        assertEquals(recurringDomain.recursThursday(), recurring.isRecursThursday(), "Recurrence flags should match domain model");
        assertEquals(recurringDomain.recursFriday(), recurring.isRecursFriday(), "Recurrence flags should match domain model");
        assertEquals(recurringDomain.recursSaturday(), recurring.isRecursSaturday(), "Recurrence flags should match domain model");
        assertEquals(recurringDomain.recursSunday(), recurring.isRecursSunday(), "Recurrence flags should match domain model");
        assertEquals(washingMachine, recurring.getAppliance(), "Appliance should match Appliance given in constructor");
    }

    @Test void testSetters_nonRecurring() {
        nonRecurring.setAppliance(washingMachine);
        nonRecurring.setActivationTime(afternoon);
        nonRecurring.setActivationDate(easter);

        assertEquals(washingMachine, nonRecurring.getAppliance(), "Appliance should be updated after setter");
        assertEquals(afternoon, nonRecurring.getActivationTime(), "Activation Time should be updated after setter");
        assertEquals(easter, nonRecurring.getActivationDate(), "Activation Date should be updated after setter");
        assertEquals(ActivationType.NON_RECURRING, nonRecurring.getActivationType(), "Activation Type should remain unchanged after setter");
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

        assertEquals(dishwasher, recurring.getAppliance(), "Appliance should be updated after setter");
        assertEquals(morning, recurring.getActivationTime(), "Activation Time should be updated after setter");
        assertFalse(recurring.isRecursMonday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursTuesday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursWednesday(), "Recurrence flags should be updated after setter");
        assertTrue(recurring.isRecursThursday(), "Recurrence flags should be updated after setter");
        assertTrue(recurring.isRecursFriday(), "Recurrence flags should be updated after setter");
        assertTrue(recurring.isRecursSaturday(), "Recurrence flags should be updated after setter");
        assertTrue(recurring.isRecursSunday(), "Recurrence flags should be updated after setter");
        assertEquals(ActivationType.RECURRING, recurring.getActivationType(), "Activation Type should remain unchanged after setter");
    }

    @Test void testSetters_nonRecurring_to_recurring() {
        nonRecurring.setActivationType(ActivationType.RECURRING);
        nonRecurring.setActivationDate(null);
        nonRecurring.setRecursMonday(true);
        nonRecurring.setRecursTuesday(true);
        nonRecurring.setRecursWednesday(false);
        nonRecurring.setRecursThursday(false);
        nonRecurring.setRecursFriday(true);
        nonRecurring.setRecursSaturday(true);
        nonRecurring.setRecursSunday(true);

        assertEquals(ActivationType.RECURRING, nonRecurring.getActivationType(), "Activation Type should be updated to RECURRING");
        assertNull(nonRecurring.getActivationDate(), "Activation Date should be updated after setter");
        assertTrue(nonRecurring.isRecursMonday(), "Recurrence flags should be updated after setter");
        assertTrue(nonRecurring.isRecursTuesday(), "Recurrence flags should be updated after setter");
        assertFalse(nonRecurring.isRecursWednesday(), "Recurrence flags should be updated after setter");
        assertFalse(nonRecurring.isRecursThursday(), "Recurrence flags should be updated after setter");
        assertTrue(nonRecurring.isRecursFriday(), "Recurrence flags should be updated after setter");
        assertTrue(nonRecurring.isRecursSaturday(), "Recurrence flags should be updated after setter");
        assertTrue(nonRecurring.isRecursSunday(), "Recurrence flags should be updated after setter");
    }

    @Test void testSetters_recurring_to_nonRecurring() {
        recurring.setActivationType(ActivationType.NON_RECURRING);
        recurring.setActivationDate(easter);
        recurring.setRecursMonday(false);
        recurring.setRecursTuesday(false);
        recurring.setRecursWednesday(false);
        recurring.setRecursThursday(false);
        recurring.setRecursFriday(false);
        recurring.setRecursSaturday(false);
        recurring.setRecursSunday(false);

        assertEquals(easter, recurring.getActivationDate(), "Activation Date should be updated after setter");
        assertFalse(recurring.isRecursMonday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursTuesday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursWednesday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursThursday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursFriday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursSaturday(), "Recurrence flags should be updated after setter");
        assertFalse(recurring.isRecursSunday(), "Recurrence flags should be updated after setter");
    }

    @Test void testProperties_nonRecurring() {
        assertNotNull(nonRecurring.applianceProperty(), "Appliance property should not be null");
        assertNotNull(nonRecurring.activationTypeProperty(), "Activation Type property should not be null");
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
        assertNotNull(recurring.activationTypeProperty(), "Activation Type property should not be null");
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
        when(washingMachine.getHouse()).thenReturn(house);
        when(house.getId()).thenReturn(houseId);

        nonRecurring.setAppliance(washingMachine);
        nonRecurring.setActivationTime(afternoon);
        nonRecurring.setActivationDate(easter);

        Activation result = nonRecurring.commit();

        assertEquals(nonRecurring.getId(), result.id(), "Domain model ID should be correct");
        assertEquals(nonRecurring.getActivationType(), result.type(), "Domain model Activation Type should be correct");
        assertEquals(washingMachine.getId(), result.applianceId(), "Domain model Appliance ID should be correct");
        assertEquals(afternoon, result.activationTime(), "Domain model Activation Time should be correct");
        assertEquals(easter, result.activationDate(), "Domain model Activation Date should be correct");
    }

    @Test void testCommit_recurring() {
        when(dishwasher.getId()).thenReturn(dishwasherId);
        when(dishwasher.getHouse()).thenReturn(house);
        when(house.getId()).thenReturn(houseId);

        recurring.setAppliance(dishwasher);
        recurring.setActivationTime(morning);
        recurring.setRecursMonday(false);
        recurring.setRecursTuesday(false);
        recurring.setRecursWednesday(false);
        recurring.setRecursThursday(true);
        recurring.setRecursFriday(true);
        recurring.setRecursSaturday(true);
        recurring.setRecursSunday(true);

        Activation result = recurring.commit();

        assertEquals(recurring.getId(), result.id(), "Domain model ID should be correct");
        assertEquals(recurring.getActivationType(), result.type(), "Domain model Activation Type should be correct");
        assertEquals(dishwasher.getId(), result.applianceId(), "Domain model Appliance ID should be correct");
        assertEquals(morning, result.activationTime(), "Domain model Activation Time should be correct");
        assertFalse(result.recursMonday(), "Domain model recurrence flags should be correct");
        assertFalse(result.recursTuesday(), "Domain model recurrence flags should be correct");
        assertFalse(result.recursWednesday(), "Domain model recurrence flags should be correct");
        assertTrue(result.recursThursday(), "Domain model recurrence flags should be correct");
        assertTrue(result.recursFriday(), "Domain model recurrence flags should be correct");
        assertTrue(result.recursSaturday(), "Domain model recurrence flags should be correct");
        assertTrue(result.recursSunday(), "Domain model recurrence flags should be correct");
    }

    @Test void testUpdateFrom_nonRecurring() {
        Activation newDomain = new Activation(washingMachineId, houseId, afternoon, easter);

        nonRecurring.updateFrom(newDomain, washingMachine);

        assertEquals(nonRecurringDomain.id(), nonRecurring.getId(), "ID should not have changed");
        assertEquals(ActivationType.NON_RECURRING, nonRecurring.getActivationType(), "Activation Type should not have changed");
        assertEquals(washingMachine, nonRecurring.getAppliance(), "Appliance should be updated");
        assertEquals(afternoon, nonRecurring.getActivationTime(), "Activation Time should be updated from domain model");
        assertEquals(easter, nonRecurring.getActivationDate(), "Activation Date should be updated from domain model");
    }

    @Test void testUpdateFrom_recurring() {
        Activation newDto = new Activation(dishwasherId, houseId, morning, false, false, false, true, true, true, true);

        recurring.updateFrom(newDto, dishwasher);

        assertEquals(recurringDomain.id(), recurring.getId(), "ID should not have changed");
        assertEquals(ActivationType.RECURRING, recurring.getActivationType(), "Activation Type should not have changed");
        assertEquals(dishwasher, recurring.getAppliance(), "Appliance should be updated");
        assertEquals(morning, recurring.getActivationTime(), "Activation Time should be updated from domain model");
        assertFalse(recurring.isRecursMonday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursTuesday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursWednesday(), "Recurrence flags should be updated from domain model");
        assertTrue(recurring.isRecursThursday(), "Recurrence flags should be updated from domain model");
        assertTrue(recurring.isRecursFriday(), "Recurrence flags should be updated from domain model");
        assertTrue(recurring.isRecursSaturday(), "Recurrence flags should be updated from domain model");
        assertTrue(recurring.isRecursSunday(), "Recurrence flags should be updated from domain model");
    }

    @Test void testUpdateFrom_nonRecurring_to_recurring() {
        Activation newDto = new Activation(dishwasherId, houseId, morning, true, true, false, false, true, true, true);

        nonRecurring.updateFrom(newDto, dishwasher);

        assertEquals(ActivationType.RECURRING, nonRecurring.getActivationType(), "Activation Type should be updated to RECURRING");
        assertNull(nonRecurring.getActivationDate(), "Activation Date should be updated from domain model");
        assertTrue(nonRecurring.isRecursMonday(), "Recurrence flags should be updated from domain model");
        assertTrue(nonRecurring.isRecursTuesday(), "Recurrence flags should be updated from domain model");
        assertFalse(nonRecurring.isRecursWednesday(), "Recurrence flags should be updated from domain model");
        assertFalse(nonRecurring.isRecursThursday(), "Recurrence flags should be updated from domain model");
        assertTrue(nonRecurring.isRecursFriday(), "Recurrence flags should be updated from domain model");
        assertTrue(nonRecurring.isRecursSaturday(), "Recurrence flags should be updated from domain model");
        assertTrue(nonRecurring.isRecursSunday(), "Recurrence flags should be updated from domain model");
    }

    @Test void testUpdateFrom_recurring_to_nonRecurring() {
        Activation newDto = new Activation(washingMachineId, houseId, afternoon, easter);

        recurring.updateFrom(newDto, washingMachine);

        assertEquals(ActivationType.NON_RECURRING, recurring.getActivationType(), "Activation Type should be updated to NON_RECURRING");
        assertEquals(easter, recurring.getActivationDate(), "Activation Date should be updated from domain model");
        assertFalse(recurring.isRecursMonday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursTuesday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursWednesday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursThursday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursFriday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursSaturday(), "Recurrence flags should be updated from domain model");
        assertFalse(recurring.isRecursSunday(), "Recurrence flags should be updated from domain model");
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
