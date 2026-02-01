package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.services.NotificationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleViewModelTest {

    @Mock private ApplianceRepository appRepo;
    @Mock private ActivationRepository actRepo;
    @Mock private NotificationService notifService;

    @Mock private Appliance appliance1;
    @Mock private Appliance appliance2;

    @Captor private ArgumentCaptor<Activation> activationCaptor;

    private ScheduleViewModel viewModel;

    @BeforeEach void setUp() {
        when(appRepo.findAll()).thenReturn(Collections.emptyList());
        viewModel = new ScheduleViewModel(appRepo, actRepo, notifService);
    }

    @Test void testLoadsAppliances() {
        List<Appliance> expectedData = List.of(appliance1, appliance2);
        when(appRepo.findAll()).thenReturn(expectedData);

        // Reinstantiate the VM to trigger the constructor again
        viewModel = new ScheduleViewModel(appRepo, actRepo, notifService);

        ObservableList<Appliance> list = viewModel.getApplianceList();

        assertEquals(2, list.size(), "ApplianceList should contain 2 items");
        assertTrue(list.contains(appliance1));
        assertTrue(list.contains(appliance2));
        verify(appRepo, times(2)).findAll(); // Called once in setUp and once here
    }

    @Test void testHandlesEmptyRepository() {
        assertTrue(viewModel.getApplianceList().isEmpty(), "ApplianceList should be empty");
    }

    @Test void testSelectedAppliance() {
        assertNull(viewModel.getSelectedAppliance(), "Should be null initially");

        viewModel.selectedApplianceProperty().set(appliance1);

        assertEquals(appliance1, viewModel.getSelectedAppliance(), "Selected appliance should match the one set");
        assertNotNull(viewModel.selectedApplianceProperty().get());
    }

    @Test void testScheduleActivation() {
        LocalDateTime targetTime = LocalDateTime.of(2025, 12, 25, 10, 0);
        viewModel.selectedApplianceProperty().set(appliance1);

        viewModel.scheduleActivation(targetTime);

        // Verify that actRepo.save() was called and capture its result
        verify(actRepo).save(activationCaptor.capture());
        Activation capturedActivation = activationCaptor.getValue();
        assertEquals(appliance1, capturedActivation.getAppliance(), "Saved activation should have the selected appliance");
        assertEquals(targetTime, capturedActivation.getActivationTime(), "Saved activation should have the target time");
        assertEquals(-1, capturedActivation.getId(), "New activation should have default ID -1");

        // Verify the notification service was called with the same object
        verify(notifService).scheduleNotification(capturedActivation);
    }
}