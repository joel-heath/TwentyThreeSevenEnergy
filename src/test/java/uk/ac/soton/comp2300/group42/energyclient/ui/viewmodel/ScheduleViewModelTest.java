package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

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
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleViewModelTest {

    @Mock Repository repo;
    ObservableList<ApplianceModel> appliances;
    @Mock ApplianceModel appliance1;
    @Mock ApplianceModel appliance2;

    @Captor ArgumentCaptor<ActivationDTO> activationCaptor;

    private ScheduleViewModel viewModel;

    @BeforeEach void setUp() {
        appliances = FXCollections.observableArrayList();
        when(repo.getAppliances()).thenReturn(appliances);

        viewModel = new ScheduleViewModel(repo);
    }

    @Test void testLoadsAppliances() {
        appliances.setAll(List.of(appliance1, appliance2));

        ObservableList<ApplianceModel> list = viewModel.getApplianceList();

        assertEquals(2, list.size(), "ApplianceList should contain 2 items");
        assertTrue(list.contains(appliance1));
        assertTrue(list.contains(appliance2));
        verify(repo, times(1)).getAppliances(); // Called once in *setUp*, not because we fetched from the VM.
    }

    @Test void testHandlesNoAppliances() {
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

        verify(repo).createActivation(activationCaptor.capture());
        ActivationDTO capturedActivation = activationCaptor.getValue();

        assertEquals(appliance1.getId(), capturedActivation.getApplianceId(), "Saved activation should have the selected appliance");
        assertEquals(targetTime, capturedActivation.getActivationTime(), "Saved activation should have the target time");
        assertNull(capturedActivation.getId(), "New activation should have an unset ID");
    }
}