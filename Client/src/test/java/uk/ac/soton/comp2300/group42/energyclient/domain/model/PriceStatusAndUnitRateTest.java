package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceStatusAndUnitRateTest {

    @Test
    void fromValue_appliesBoundaryThresholdsCorrectly() {
        assertEquals(PriceStatus.CHEAP, PriceStatus.fromValue(-10.0));
        assertEquals(PriceStatus.CHEAP, PriceStatus.fromValue(0.0));
        assertEquals(PriceStatus.CHEAP, PriceStatus.fromValue(19.9999));

        assertEquals(PriceStatus.AVERAGE, PriceStatus.fromValue(20.0));
        assertEquals(PriceStatus.AVERAGE, PriceStatus.fromValue(25.0));
        assertEquals(PriceStatus.AVERAGE, PriceStatus.fromValue(29.9999));

        assertEquals(PriceStatus.EXPENSIVE, PriceStatus.fromValue(30.0));
        assertEquals(PriceStatus.EXPENSIVE, PriceStatus.fromValue(100.0));
    }

    @Test
    void unitRate_getPriceStatus_usesPriceStatusClassification() {
        ZonedDateTime now = ZonedDateTime.parse("2026-04-01T12:00:00Z");

        assertEquals(PriceStatus.CHEAP, new UnitRate(19.9999, now).getPriceStatus());
        assertEquals(PriceStatus.AVERAGE, new UnitRate(20.0, now).getPriceStatus());
        assertEquals(PriceStatus.EXPENSIVE, new UnitRate(30.0, now).getPriceStatus());
    }
}
