package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivationModelTest {
    ActivationDTO dto;
    ActivationModel model;

    final LocalDateTime christmas = LocalDateTime.of(2025, 12, 25, 12, 0);
    final LocalDateTime easter = LocalDateTime.of(2026, 4, 5, 13, 30);

    final Long washingMachineId = 1L;
    final Long dishwasherId = 2L;

    @Mock ApplianceModel washingMachine;
    @Mock ApplianceModel dishwasher;

    @BeforeEach void setUp() {
        dto = new ActivationDTO(10L, washingMachineId, christmas);
        model = new ActivationModel(dto, washingMachine);
    }

    @Test void testGetters() {
        when(washingMachine.getId()).thenReturn(washingMachineId);

        assertEquals(dto.getId(), model.getId(), "ID should match DTO");
        assertEquals(dto.getActivationTime(), model.getActivationTime(), "Activation Time should match DTO");
        assertEquals(dto.getApplianceId(), model.getAppliance().getId(), "Appliance ID should match DTO");
    }

    @Test void testSetters() {
        model.setAppliance(dishwasher);
        model.setActivationTime(easter);

        assertEquals(dishwasher, model.getAppliance());
        assertEquals(easter, model.getActivationTime());
    }

    @Test void testProperties() {
        assertNotNull(model.activationTimeProperty());
        assertNotNull(model.applianceProperty());
    }

    @Test void testCommit() {
        when(dishwasher.getId()).thenReturn(dishwasherId);

        model.setAppliance(dishwasher);
        model.setActivationTime(easter);
        ActivationDTO result = model.commit();

        assertEquals(dto.getId(), result.getId(), "DTO ID should be unchanged");
        assertEquals(dishwasher.getId(), result.getApplianceId(), "DTO Appliance ID should be updated");
        assertEquals(easter, result.getActivationTime(), "DTO Activation Time should be updated");
    }

    @Test void testUpdateFrom() {
        ActivationDTO newDto = new ActivationDTO(20L, dishwasherId, easter);

        model.updateFrom(newDto, dishwasher); // should it throw an exception on incorrect id?

        assertEquals(dto.getId(), model.getId(), "Model ID should not have changed");
        assertEquals(dishwasher, model.getAppliance(), "Model Appliance should be updated from new DTO");
        assertEquals(easter, model.getActivationTime(), "Model Activation Time should be updated from new DTO");
    }
}
