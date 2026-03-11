package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpcomingActivationsViewModelTest {

    @Mock private ActivationService mockActivationService;
    @Mock private ApplianceStore mockApplianceStore;
    @Mock private ObservableActivation mockActivation1;
    @Mock private ObservableActivation mockActivation2;
    @Mock private ObservableAppliance mockAppliance;

    private UpcomingActivationsViewModel viewModel;

    @BeforeEach
    void setUp() {
        // Prepare dummy lists for the store and service
        ObservableList<ObservableAppliance> dummyAppliances = FXCollections.observableArrayList(mockAppliance);
        ObservableList<ObservableActivation> dummyActivations = FXCollections.observableArrayList(mockActivation1, mockActivation2);

        when(mockApplianceStore.getAll()).thenReturn(dummyAppliances);
        when(mockActivationService.getAll()).thenReturn(dummyActivations);

        // Stub dates to test sorting logic: mockActivation2 is earlier than mockActivation1
        when(mockActivation1.getNextActivationDateTime()).thenReturn(LocalDateTime.of(2025, 1, 2, 10, 0));
        when(mockActivation2.getNextActivationDateTime()).thenReturn(LocalDateTime.of(2025, 1, 1, 10, 0));

        viewModel = new UpcomingActivationsViewModel(mockActivationService, mockApplianceStore);
    }

    @Test
    void constructor_initialisesLists_sortsActivations_andTriggersAsyncRefresh() {
        // Verify appliances passed through correctly
        assertEquals(1, viewModel.getAppliances().size());
        assertEquals(mockAppliance, viewModel.getAppliances().getFirst());

        // Verify activations are sorted by date (mock2 is earlier)
        assertEquals(2, viewModel.getActivations().size());
        assertEquals(mockActivation2, viewModel.getActivations().get(0));
        assertEquals(mockActivation1, viewModel.getActivations().get(1));

        // Verify async refresh call. Timeout is necessary as it runs on a background thread.
        verify(mockActivationService, timeout(1000).times(1)).refreshAllAsync();
    }

    @Test
    void removeActivation_delegatesToDeleteService() {
        viewModel.removeActivation(mockActivation1);
        verify(mockActivationService, times(1)).delete(mockActivation1);
    }

    @Test
    void updateActivation_whenRecurring_setsRecursionFields_clearsDate_andSaves() {
        LocalTime time = LocalTime.of(14, 30);
        LocalDate date = LocalDate.of(2025, 5, 10);

        // Act: set to recurring (isRecurring = true)
        viewModel.updateActivation(
                mockActivation1, mockAppliance, time, date,
                true, false, true, false, false, false, false,
                true
        );

        verify(mockActivation1).setAppliance(mockAppliance);
        verify(mockActivation1).setActivationTime(time);

        // Recurring logic: date must be null, days must match inputs
        verify(mockActivation1).setActivationDate(null);
        verify(mockActivation1).setRecursMonday(true);
        verify(mockActivation1).setRecursTuesday(false);
        verify(mockActivation1).setRecursWednesday(true);
        verify(mockActivation1).setRecursThursday(false);
        verify(mockActivation1).setRecursFriday(false);
        verify(mockActivation1).setRecursSaturday(false);
        verify(mockActivation1).setRecursSunday(false);

        verify(mockActivationService, times(1)).save(mockActivation1);
    }

    @Test
    void updateActivation_whenNotRecurring_setsDate_clearsRecursionFields_andSaves() {
        LocalTime time = LocalTime.of(8, 0);
        LocalDate date = LocalDate.of(2025, 6, 15);

        // Act: set to non-recurring (isRecurring = false), passing true for days to ensure they are ignored
        viewModel.updateActivation(
                mockActivation1, mockAppliance, time, date,
                true, true, true, true, true, true, true,
                false
        );

        verify(mockActivation1).setAppliance(mockAppliance);
        verify(mockActivation1).setActivationTime(time);

        // Non-recurring logic: date must be set, all recursion days forced to false
        verify(mockActivation1).setActivationDate(date);
        verify(mockActivation1).setRecursMonday(null);
        verify(mockActivation1).setRecursTuesday(null);
        verify(mockActivation1).setRecursWednesday(null);
        verify(mockActivation1).setRecursThursday(null);
        verify(mockActivation1).setRecursFriday(null);
        verify(mockActivation1).setRecursSaturday(null);
        verify(mockActivation1).setRecursSunday(null);

        verify(mockActivationService, times(1)).save(mockActivation1);
    }
}