package uk.ac.soton.comp2300.group42.energyclient.domain.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnergyCalculatorTest {

    @Test
    void testConvertJoulesToPounds() {
        EnergyCalculator calc = new EnergyCalculator();
        int inputJoules = 1000;

        double result = calc.convertJoulesToPounds(inputJoules);

        assertEquals(0.42, result, 0.0001, "Conversion should be correct within a small delta");
    }

    @Test
    void testZeroJoules() {
        EnergyCalculator calc = new EnergyCalculator();
        assertEquals(0.0, calc.convertJoulesToPounds(0), "0 Joules should be £0.00");
    }
}