package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleViewModelTest {

    @Mock ModelFactory modelFactory;
    @Mock private ApplianceClient applianceClient;
    @Mock private ActivationClient activationClient;
    @Mock private NotificationService notifService;

    @Mock private ApplianceDTO appliance1DTO;
    @Mock private ApplianceDTO appliance2DTO;
    @Mock private ApplianceModel appliance1;
    @Mock private ApplianceModel appliance2;

    @Captor private ArgumentCaptor<ActivationDTO> activationCaptor;

    private ScheduleViewModel viewModel;

    @BeforeEach void setUp() {
        when(applianceClient.findAll()).thenReturn(Collections.emptyList());
        viewModel = new ScheduleViewModel(modelFactory, applianceClient, activationClient, notifService);
    }

    @Test void testLoadsAppliances() {
        List<ApplianceDTO> expectedData = List.of(appliance1DTO, appliance2DTO);
        when(applianceClient.findAll()).thenReturn(expectedData);

        // Reinstantiate the VM to trigger the constructor again
        viewModel = new ScheduleViewModel(modelFactory, applianceClient, activationClient, notifService);

        ObservableList<ApplianceModel> list = viewModel.getApplianceList();

        assertEquals(2, list.size(), "ApplianceList should contain 2 items");
        assertTrue(list.contains(appliance1));
        assertTrue(list.contains(appliance2));
        verify(applianceClient, times(2)).findAll(); // Called once in setUp and once here
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
        verify(activationClient).save(activationCaptor.capture());
        ActivationDTO capturedActivation = activationCaptor.getValue();
        // assertEquals(appliance1, capturedActivation.getAppliance(), "Saved activation should have the selected appliance");
        assertEquals(targetTime, capturedActivation.getActivationTime(), "Saved activation should have the target time");
        assertEquals(-1, capturedActivation.getId(), "New activation should have default ID -1");

        // Verify the notification service was called with the same object
        // verify(notifService).scheduleNotification(capturedActivation); cannot do this with a model
    }
}