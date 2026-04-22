package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.common.Role;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
}
