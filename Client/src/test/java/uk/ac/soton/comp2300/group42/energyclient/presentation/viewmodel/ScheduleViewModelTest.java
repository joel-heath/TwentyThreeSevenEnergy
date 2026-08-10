package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleViewModelTest {

    @Mock private ActivationService activationService;
    @Mock private ApplianceStore applianceStore;

    private ObservableAppliance appliance;
    private ScheduleViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        ObservablePreferences preferences = new ObservablePreferences(new Preferences(), house);
        appliance = new ObservableAppliance(new Appliance(10L, 1L, "Kettle"), house);

        when(applianceStore.getAll()).thenReturn(FXCollections.observableArrayList(appliance));

        viewModel = new ScheduleViewModel(activationService, applianceStore, preferences);
    }

    @Test
    void loadData_refreshesAppliances() {
        viewModel.loadData();
        verify(applianceStore).refreshAllAsync();
    }

    @Test
    void scheduleActivation_withoutSelectedAppliance_setsError() {
        viewModel.scheduleActivation();

        assertEquals("Failed to schedule, no appliance selected", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
    }

    @Test
    void scheduleActivation_forPastDate_setsError() {
        viewModel.selectedApplianceProperty().set(appliance);
        viewModel.isRecurringProperty().set(false);
        viewModel.dateProperty().set(LocalDate.now().minusDays(1));

        viewModel.scheduleActivation();

        assertEquals("Failed to schedule, selected date is in the past", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
    }

    @Test
    void scheduleActivation_recurringWithoutDays_setsError() {
        viewModel.selectedApplianceProperty().set(appliance);
        viewModel.isRecurringProperty().set(true);

        viewModel.scheduleActivation();

        assertEquals("Failed to schedule, no recurrence days selected", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
    }

    @Test
    void scheduleActivation_nonRecurringBuildsActivationAndSetsSuccessMessage() {
        when(activationService.create(any(Activation.class)))
                .thenReturn(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(7, 30)));
        viewModel.selectedApplianceProperty().set(appliance);
        viewModel.isRecurringProperty().set(false);
        viewModel.dateProperty().set(LocalDate.now().plusDays(1));
        viewModel.hourProperty().set(7);
        viewModel.minuteProperty().set(30);

        viewModel.scheduleActivation();

        ArgumentCaptor<Activation> captor = ArgumentCaptor.forClass(Activation.class);
        verify(activationService).create(captor.capture());
        Activation activation = captor.getValue();
        assertEquals(ActivationType.NON_RECURRING, activation.type());
        assertEquals(10L, activation.applianceId());
        assertEquals(1L, activation.houseId());
        assertEquals(LocalDate.now().plusDays(1), activation.activationDate());
        assertEquals(LocalTime.of(7, 30), activation.activationTime());
        assertEquals("response-success", viewModel.responseStyleClassProperty().get());
        assertTrue(viewModel.responseMessageProperty().get().contains("Kettle scheduled for 07:30"));
    }

    @Test
    void scheduleActivation_recurringBuildsRecurringActivation() {
        when(activationService.create(any(Activation.class))).thenReturn(LocalDateTime.now().plusDays(2));
        viewModel.selectedApplianceProperty().set(appliance);
        viewModel.isRecurringProperty().set(true);
        viewModel.recursMondayProperty().set(true);
        viewModel.hourProperty().set(9);
        viewModel.minuteProperty().set(15);

        viewModel.scheduleActivation();

        ArgumentCaptor<Activation> captor = ArgumentCaptor.forClass(Activation.class);
        verify(activationService).create(captor.capture());
        Activation activation = captor.getValue();
        assertEquals(ActivationType.RECURRING, activation.type());
        assertNull(activation.activationDate());
        assertTrue(activation.recursMonday());
        assertEquals(LocalTime.of(9, 15), activation.activationTime());
    }

    @Test
    void scheduleActivation_whenServiceFails_setsErrorMessage() {
        doThrow(apiException("network")).when(activationService).create(any(Activation.class));
        viewModel.selectedApplianceProperty().set(appliance);
        viewModel.isRecurringProperty().set(false);
        viewModel.dateProperty().set(LocalDate.now().plusDays(1));

        viewModel.scheduleActivation();

        assertEquals("Failed to schedule: network", viewModel.responseMessageProperty().get());
        assertEquals("response-error", viewModel.responseStyleClassProperty().get());
    }

    private static ApiException apiException(String message) {
        return new ApiException(Instant.now(), 500, "Internal Server Error", message, "/activations");
    }
}

