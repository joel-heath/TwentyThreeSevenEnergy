package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ActivationDTOTest {
    final Long dishwasherId = 1L;
    final Long washingMachineId = 2L;

    final LocalDateTime christmas = LocalDateTime.of(2025, 12, 25, 12, 0);
    final LocalDateTime easter = LocalDateTime.of(2026, 4, 5, 13, 30);

    @Test void testGetters() {
        ActivationDTO act = new ActivationDTO(dishwasherId, christmas);

        assertNull(act.getId());
        assertEquals(dishwasherId, act.getApplianceId());
        assertEquals(christmas, act.getActivationTime());
    }

    @Test void testSetters() {
        ActivationDTO act = new ActivationDTO(dishwasherId, christmas);

        act.setApplianceId(washingMachineId);
        act.setActivationTime(easter);

        assertEquals(washingMachineId, act.getApplianceId());
        assertEquals(easter, act.getActivationTime());
    }
}
