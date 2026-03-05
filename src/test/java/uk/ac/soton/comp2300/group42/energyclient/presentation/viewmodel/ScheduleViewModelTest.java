package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleViewModelTest {

    @Mock private ActivationService activationService;
    @Mock private ApplianceStore applianceStore;
    @Mock private ObservablePreferences preferences;
    @Mock private ObservableHouse activeHouse;
    @Mock private ObservableAppliance appliance;

    private ObservableList<ObservableAppliance> applianceList;
    private ScheduleViewModel viewModel;

    @BeforeEach
    void setUp() {
        applianceList = FXCollections.observableArrayList(appliance);
        when(applianceStore.getAll()).thenReturn(applianceList);
        viewModel = new ScheduleViewModel(activationService, applianceStore, preferences);
    }

    @Test
    void constructor_initialisesPropertiesAndAppliances() {
        assertSame(applianceList, viewModel.getApplianceList());
        assertFalse(viewModel.isRecurringProperty().get());
        assertFalse(viewModel.recursMondayProperty().get());
        assertFalse(viewModel.recursSundayProperty().get());
    }

    @Test
    void scheduleActivation_nonRecurring_buildsNonRecurringActivation() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        LocalDateTime expected = LocalDateTime.of(2026, 3, 10, 9, 15);
        when(activationService.create(any(Activation.class))).thenReturn(expected);
        when(preferences.getActiveHouse()).thenReturn(activeHouse);
        when(activeHouse.getId()).thenReturn(88L);
        when(appliance.getId()).thenReturn(44L);

        viewModel.selectedApplianceProperty().set(appliance);
        viewModel.hourProperty().set(9);
        viewModel.minuteProperty().set(15);
        viewModel.dateProperty().set(date);
        viewModel.isRecurringProperty().set(false);

        LocalDateTime result = viewModel.scheduleActivation();

        ArgumentCaptor<Activation> captor = ArgumentCaptor.forClass(Activation.class);
        org.mockito.Mockito.verify(activationService).create(captor.capture());
        Activation sent = captor.getValue();
        assertEquals(ActivationType.NON_RECURRING, sent.type());
        assertEquals(44L, sent.applianceId());
        assertEquals(88L, sent.houseId());
        assertEquals(date, sent.activationDate());
        assertEquals(expected, result);
    }

    @Test
    void scheduleActivation_recurring_buildsRecurringActivation() {
        LocalDateTime expected = LocalDateTime.of(2026, 3, 11, 8, 30);
        when(activationService.create(any(Activation.class))).thenReturn(expected);
        when(preferences.getActiveHouse()).thenReturn(activeHouse);
        when(activeHouse.getId()).thenReturn(88L);
        when(appliance.getId()).thenReturn(44L);

        viewModel.selectedApplianceProperty().set(appliance);
        viewModel.hourProperty().set(8);
        viewModel.minuteProperty().set(30);
        viewModel.isRecurringProperty().set(true);
        viewModel.recursMondayProperty().set(true);
        viewModel.recursWednesdayProperty().set(true);
        viewModel.recursFridayProperty().set(true);

        LocalDateTime result = viewModel.scheduleActivation();

        ArgumentCaptor<Activation> captor = ArgumentCaptor.forClass(Activation.class);
        org.mockito.Mockito.verify(activationService).create(captor.capture());
        Activation sent = captor.getValue();
        assertEquals(ActivationType.RECURRING, sent.type());
        assertEquals(44L, sent.applianceId());
        assertEquals(88L, sent.houseId());
        assertNull(sent.activationDate());
        assertEquals(true, sent.recursMonday());
        assertEquals(true, sent.recursWednesday());
        assertEquals(true, sent.recursFriday());
        assertEquals(expected, result);
    }
}
