package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleViewModelTest {

    @Mock
    IDoEverything repo;
    ObservableList<ObservableAppliance> appliances;
    @Mock
    ObservableAppliance appliance1;
    @Mock
    ObservableAppliance appliance2;

    @Captor ArgumentCaptor<ActivationDTO> activationCaptor;

    private ScheduleViewModel viewModel;

    @BeforeEach void setUp() {
        appliances = FXCollections.observableArrayList();
        when(repo.getAppliances()).thenReturn(appliances);

        viewModel = new ScheduleViewModel(repo);
    }

    @Test void testLoadsAppliances() {
        appliances.setAll(List.of(appliance1, appliance2));

        ObservableList<ObservableAppliance> list = viewModel.getApplianceList();

        assertEquals(2, list.size(), "ApplianceList should contain 2 items");
        assertTrue(list.contains(appliance1));
        assertTrue(list.contains(appliance2));
        verify(repo, times(1)).getAppliances(); // Called once in *setUp*, not because we fetched from the VM.
    }

    @Test void testHandlesNoAppliances() {
        assertTrue(viewModel.getApplianceList().isEmpty(), "ApplianceList should be empty");
    }

    @Test void testProperties() {
        assertNotNull(viewModel.selectedApplianceProperty(), "Selected Appliance property should be initialized");
        assertNotNull(viewModel.hourProperty(), "Hour property should be initialized");
        assertNotNull(viewModel.minuteProperty(), "Minute property should be initialized");
        assertNotNull(viewModel.dateProperty(), "Date property should be initialized");
        assertNotNull(viewModel.recursMondayProperty(), "Recurs on Mondays property should be initialized");
        assertNotNull(viewModel.recursTuesdayProperty(), "Recurs on Tuesdays property should be initialized");
        assertNotNull(viewModel.recursWednesdayProperty(), "Recurs on Wednesdays property should be initialized");
        assertNotNull(viewModel.recursThursdayProperty(), "Recurs on Thursdays property should be initialized");
        assertNotNull(viewModel.recursFridayProperty(), "Recurs on Fridays property should be initialized");
        assertNotNull(viewModel.recursSaturdayProperty(), "Recurs on Saturdays property should be initialized");
        assertNotNull(viewModel.recursSundayProperty(), "Recurs on Sundays property should be initialized");
        assertNotNull(viewModel.isRecurringProperty(), "Is recurring property should be initialized");
    }

    @Test void testScheduleActivation_nonRecurring() {
        LocalTime targetTime = LocalTime.of(10, 0);
        LocalDate targetDate = LocalDate.of(2025, 12, 25);

        viewModel.selectedApplianceProperty().set(appliance1);
        viewModel.hourProperty().set(targetTime.getHour());
        viewModel.minuteProperty().set(targetTime.getMinute());
        viewModel.dateProperty().set(targetDate);
        viewModel.isRecurringProperty().set(false);
        viewModel.recursMondayProperty().set(true); // Should be ignored for non-recurring Activations
        viewModel.recursThursdayProperty().set(true); // Should be ignored for non-recurring Activations

        viewModel.scheduleActivation();

        verify(repo, description("Create Activation should have been called on the Repository")).createActivation(activationCaptor.capture());
        ActivationDTO capturedActivation = activationCaptor.getValue();

        assertNull(capturedActivation.getId(), "New activation should have an unset ID");
        assertEquals(appliance1.getId(), capturedActivation.getApplianceId(), "Saved activation should have the selected appliance");
        assertEquals(targetTime, capturedActivation.getActivationTime(), "Saved activation should have the selected time");
        assertEquals(targetDate, capturedActivation.getActivationDate(), "Saved activation should have the selected date");
        assertFalse(capturedActivation.isRecursMonday(), "Saved activation should not be recurring");
        assertFalse(capturedActivation.isRecursTuesday(), "Saved activation should not be recurring");
        assertFalse(capturedActivation.isRecursWednesday(), "Saved activation should not be recurring");
        assertFalse(capturedActivation.isRecursThursday(), "Saved activation should not be recurring");
        assertFalse(capturedActivation.isRecursFriday(), "Saved activation should not be recurring");
        assertFalse(capturedActivation.isRecursSaturday(), "Saved activation should not be recurring");
        assertFalse(capturedActivation.isRecursSunday(), "Saved activation should not be recurring");
    }

    @Test void testScheduleActivation_recurring() {
        LocalTime targetTime = LocalTime.of(10, 0);

        viewModel.selectedApplianceProperty().set(appliance1);
        viewModel.hourProperty().set(targetTime.getHour());
        viewModel.minuteProperty().set(targetTime.getMinute());
        viewModel.dateProperty().set(LocalDate.of(2025, 12, 25)); // Should be ignored for recurring Activations
        viewModel.isRecurringProperty().set(true);
        viewModel.recursMondayProperty().set(true);
        viewModel.recursThursdayProperty().set(true);

        viewModel.scheduleActivation();

        verify(repo, description("Create Activation should have been called on the Repository")).createActivation(activationCaptor.capture());
        ActivationDTO capturedActivation = activationCaptor.getValue();

        assertNull(capturedActivation.getId(), "New activation should have an unset ID");
        assertEquals(appliance1.getId(), capturedActivation.getApplianceId(), "Saved activation should have the selected appliance");
        assertEquals(targetTime, capturedActivation.getActivationTime(), "Saved activation should have the selected time");
        assertNull(capturedActivation.getActivationDate(), "Saved activation should not have a date set");
        assertTrue(capturedActivation.isRecursMonday(), "Saved activation should have the selected recurrence flags");
        assertFalse(capturedActivation.isRecursTuesday(), "Saved activation should have the selected recurrence flags");
        assertFalse(capturedActivation.isRecursWednesday(), "Saved activation should have the selected recurrence flags");
        assertTrue(capturedActivation.isRecursThursday(), "Saved activation should have the selected recurrence flags");
        assertFalse(capturedActivation.isRecursFriday(), "Saved activation should have the selected recurrence flags");
        assertFalse(capturedActivation.isRecursSaturday(), "Saved activation should have the selected recurrence flags");
        assertFalse(capturedActivation.isRecursSunday(), "Saved activation should have the selected recurrence flags");
    }
}