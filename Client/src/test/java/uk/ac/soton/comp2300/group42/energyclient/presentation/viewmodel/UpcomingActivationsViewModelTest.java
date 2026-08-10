package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpcomingActivationsViewModelTest {

    @Mock private ActivationService activationService;
    @Mock private ApplianceStore applianceStore;

    private ObservableAppliance appliance;
    private ObservableActivation laterActivation;
    private ObservableActivation earlierActivation;
    private ObservableList<ObservableActivation> activationList;
    private UpcomingActivationsViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER));
        appliance = new ObservableAppliance(new Appliance(10L, 1L, "Kettle"), house);

        laterActivation = new ObservableActivation(
                new Activation(100L, 10L, 1L, ActivationType.NON_RECURRING, LocalTime.of(10, 0), LocalDate.now().plusDays(2), null, null, null, null, null, null, null),
                appliance
        );
        earlierActivation = new ObservableActivation(
                new Activation(101L, 10L, 1L, ActivationType.NON_RECURRING, LocalTime.of(9, 0), LocalDate.now().plusDays(1), null, null, null, null, null, null, null),
                appliance
        );

        activationList = FXCollections.observableArrayList(laterActivation, earlierActivation);

        when(activationService.getAll()).thenReturn(activationList);
        when(applianceStore.getAll()).thenReturn(FXCollections.observableArrayList(appliance));

        viewModel = new UpcomingActivationsViewModel(activationService, applianceStore);
    }

    @Test
    void constructor_exposesAppliancesAndSortedActivations() {
        assertEquals(1, viewModel.getAppliances().size());
        assertSame(appliance, viewModel.getAppliances().getFirst());

        assertEquals(2, viewModel.getActivations().size());
        assertSame(earlierActivation, viewModel.getActivations().get(0));
        assertSame(laterActivation, viewModel.getActivations().get(1));
    }

    @Test
    void refreshActivationsAsync_delegatesToService() {
        CompletableFuture<Void> refreshFuture = CompletableFuture.completedFuture(null);
        when(activationService.refreshAllAsync()).thenReturn(refreshFuture);

        CompletableFuture<Void> result = viewModel.refreshActivationsAsync();

        assertSame(refreshFuture, result);
        verify(activationService).refreshAllAsync();
    }

    @Test
    void selectActivation_updatesSelectedProperty() {
        assertNull(viewModel.selectedActivationProperty().get());

        viewModel.selectActivation(earlierActivation);
        assertSame(earlierActivation, viewModel.selectedActivationProperty().get());

        viewModel.selectActivation(null);
        assertNull(viewModel.selectedActivationProperty().get());
    }
}
