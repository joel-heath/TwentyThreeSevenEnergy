package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ActivationDTOTest {
    @Mock ApplianceDTO dishwasher;
    @Mock ApplianceDTO washingMachine;

    final LocalDateTime christmas = LocalDateTime.of(2025, 12, 25, 12, 0);
    final LocalDateTime easter = LocalDateTime.of(2026, 4, 5, 13, 30);

    @Test void testGetters() {
        ActivationDTO act = new ActivationDTO(dishwasher, christmas);

        assertNull(act.getId());
        assertEquals(dishwasher, act.getAppliance());
        assertEquals(christmas, act.getActivationTime());
    }

    @Test void testSetters() {
        ActivationDTO act = new ActivationDTO(dishwasher, christmas);

        act.setAppliance(washingMachine);
        act.setActivationTime(easter);

        assertEquals(washingMachine, act.getAppliance());
        assertEquals(easter, act.getActivationTime());
    }
}
