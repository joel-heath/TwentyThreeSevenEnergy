package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivationEditViewModelTest {

    @Mock private ActivationService activationService;
    @Mock private ApplianceStore applianceStore;

    private ActivationEditViewModel viewModel;
    private ObservableAppliance appliance1;
    private ObservableAppliance appliance2;
    private ObservableActivation activation;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        appliance1 = new ObservableAppliance(new Appliance(10L, 1L, "Kettle"), house);
        appliance2 = new ObservableAppliance(new Appliance(11L, 1L, "Toaster"), house);

        when(applianceStore.getAll()).thenReturn(FXCollections.observableArrayList(appliance1, appliance2));

        viewModel = new ActivationEditViewModel(activationService, applianceStore);
        activation = new ObservableActivation(
            new Activation(100L, 10L, 1L, ActivationType.NON_RECURRING, LocalTime.of(8, 30), LocalDate.now().plusDays(1), null, null, null, null, null, null, null),
            appliance1
        );
    }

    @Test
    void loadActivation_populatesEditableProperties() {
        viewModel.loadActivation(activation);

        assertEquals(appliance1, viewModel.selectedApplianceProperty().get());
        assertEquals(8, viewModel.hourProperty().get());
        assertEquals(30, viewModel.minuteProperty().get());
        assertEquals(LocalDate.now().plusDays(1), viewModel.dateProperty().get());
        assertFalse(viewModel.isRecurringProperty().get());
    }

    @Test
    void saveChanges_withoutSelectedAppliance_returnsFalse() {
        viewModel.loadActivation(activation);
        viewModel.selectedApplianceProperty().set(null);

        boolean success = viewModel.saveChanges();

        assertFalse(success);
        assertEquals("Failed to schedule, no appliance selected", viewModel.responseMessageProperty().get());
        verify(activationService, never()).save(activation);
    }

    @Test
    void saveChanges_withRecurringSelection_updatesActivationAndSaves() {
        viewModel.loadActivation(activation);
        viewModel.selectedApplianceProperty().set(appliance2);
        viewModel.isRecurringProperty().set(true);
        viewModel.recursMondayProperty().set(true);
        viewModel.hourProperty().set(6);
        viewModel.minuteProperty().set(45);

        boolean success = viewModel.saveChanges();

        assertTrue(success);
        verify(activationService).save(activation);
        assertEquals(appliance2, activation.getAppliance());
        assertEquals(LocalTime.of(6, 45), activation.getActivationTime());
        assertNull(activation.getActivationDate());
        assertTrue(activation.isRecursMonday());
    }

    @Test
    void loadActivation_withRecurringActivation_setsRecurringDateFallbackAndFlags() {
        ObservableActivation recurringActivation = new ObservableActivation(
                new Activation(200L, 10L, 1L, ActivationType.RECURRING, LocalTime.of(7, 15), null, true, false, true, false, true, false, true),
                appliance1
        );

        viewModel.loadActivation(recurringActivation);

        assertTrue(viewModel.isRecurringProperty().get());
        assertEquals(LocalDate.now(), viewModel.dateProperty().get());
        assertTrue(viewModel.recursMondayProperty().get());
        assertTrue(viewModel.recursWednesdayProperty().get());
        assertTrue(viewModel.recursFridayProperty().get());
        assertTrue(viewModel.recursSundayProperty().get());
        assertFalse(viewModel.recursTuesdayProperty().get());
        assertFalse(viewModel.recursThursdayProperty().get());
        assertFalse(viewModel.recursSaturdayProperty().get());
    }

    @Test
    void saveChanges_recurringWithoutDays_returnsFalse() {
        viewModel.loadActivation(activation);
        viewModel.isRecurringProperty().set(true);
        viewModel.recursMondayProperty().set(false);
        viewModel.recursTuesdayProperty().set(false);
        viewModel.recursWednesdayProperty().set(false);
        viewModel.recursThursdayProperty().set(false);
        viewModel.recursFridayProperty().set(false);
        viewModel.recursSaturdayProperty().set(false);
        viewModel.recursSundayProperty().set(false);

        boolean success = viewModel.saveChanges();

        assertFalse(success);
        assertEquals("Failed to schedule, no recurrence days selected", viewModel.responseMessageProperty().get());
    }

    @Test
    void saveChanges_nonRecurringPastDate_returnsFalse() {
        viewModel.loadActivation(activation);
        viewModel.isRecurringProperty().set(false);
        viewModel.dateProperty().set(LocalDate.now().minusDays(1));

        boolean success = viewModel.saveChanges();

        assertFalse(success);
        assertEquals("Failed to schedule, selected date is in the past", viewModel.responseMessageProperty().get());
    }

    @Test
    void saveChanges_nonRecurring_clearsRecurrenceFlagsAndSaves() {
        ObservableActivation recurringActivation = new ObservableActivation(
                new Activation(300L, 10L, 1L, ActivationType.RECURRING, LocalTime.of(6, 0), null, true, true, true, true, true, true, true),
                appliance1
        );
        viewModel.loadActivation(recurringActivation);

        LocalDate targetDate = LocalDate.now().plusDays(2);
        viewModel.selectedApplianceProperty().set(appliance2);
        viewModel.isRecurringProperty().set(false);
        viewModel.dateProperty().set(targetDate);
        viewModel.hourProperty().set(9);
        viewModel.minuteProperty().set(5);

        boolean success = viewModel.saveChanges();

        assertTrue(success);
        verify(activationService).save(recurringActivation);
        assertEquals(appliance2, recurringActivation.getAppliance());
        assertEquals(LocalTime.of(9, 5), recurringActivation.getActivationTime());
        assertEquals(targetDate, recurringActivation.getActivationDate());
        assertNull(recurringActivation.isRecursMonday());
        assertNull(recurringActivation.isRecursTuesday());
        assertNull(recurringActivation.isRecursWednesday());
        assertNull(recurringActivation.isRecursThursday());
        assertNull(recurringActivation.isRecursFriday());
        assertNull(recurringActivation.isRecursSaturday());
        assertNull(recurringActivation.isRecursSunday());
    }

    @Test
    void saveChanges_whenServiceThrows_setsErrorAndReturnsFalse() {
        viewModel.loadActivation(activation);
        doThrow(new ApiException(Instant.now(), 500, "Error", "boom", "/activations"))
                .when(activationService).save(activation);

        boolean success = viewModel.saveChanges();

        assertFalse(success);
        assertTrue(viewModel.hasResponseErrorProperty().get());
        assertEquals("Failed to update: boom", viewModel.responseMessageProperty().get());
    }

    @Test
    void deleteActivation_withoutCurrentActivation_doesNothing() {
        viewModel.deleteActivation();

        verify(activationService, never()).delete(any());
    }

    @Test
    void deleteActivation_withCurrentActivation_deletesActivation() {
        viewModel.loadActivation(activation);

        viewModel.deleteActivation();

        verify(activationService).delete(activation);
    }

    @Test
    void propertyAccessors_exposeAllProperties() {
        assertEquals(viewModel.selectedApplianceProperty(), viewModel.selectedApplianceProperty());
        assertNotNull(viewModel.getApplianceList());
        assertNotNull(viewModel.hourProperty());
        assertNotNull(viewModel.minuteProperty());
        assertNotNull(viewModel.dateProperty());
        assertNotNull(viewModel.recursMondayProperty());
        assertNotNull(viewModel.recursTuesdayProperty());
        assertNotNull(viewModel.recursWednesdayProperty());
        assertNotNull(viewModel.recursThursdayProperty());
        assertNotNull(viewModel.recursFridayProperty());
        assertNotNull(viewModel.recursSaturdayProperty());
        assertNotNull(viewModel.recursSundayProperty());
        assertNotNull(viewModel.isRecurringProperty());
        assertNotNull(viewModel.responseMessageProperty());
        assertNotNull(viewModel.hasResponseErrorProperty());
    }
}
