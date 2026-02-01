package uk.ac.soton.comp2300.group42.energyclient.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ActivationTest {
    @Mock Appliance dishwasher;
    @Mock Appliance washingMachine;

    final LocalDateTime christmas = LocalDateTime.of(2025, 12, 25, 12, 0);
    final LocalDateTime easter = LocalDateTime.of(2026, 4, 5, 13, 30);

    @Test void testGetters() {
        Activation act = new Activation(1, dishwasher, christmas);

        assertEquals(1, act.getId());
        assertEquals(dishwasher, act.getAppliance());
        assertEquals(christmas, act.getActivationTime());
    }

    @Test void testSetters() {
        Activation act = new Activation(1, dishwasher, christmas);

        act.setAppliance(washingMachine);
        act.setActivationTime(easter);

        assertEquals(washingMachine, act.getAppliance());
        assertEquals(easter, act.getActivationTime());
    }
}
