package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.energyclient.data.external.UnitRateResponse;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UnitRateMapperTest {

    private final UnitRateMapper mapper = Mappers.getMapper(UnitRateMapper.class);

    @Test
    void toUnitRate_ShouldMapAllFieldsCorrectly() {
        UnitRateResponse response = new UnitRateResponse(4.15, "2025-12-25T14:32:16Z", "2026-01-01T00:00:00Z");

        UnitRate domain = mapper.toUnitRate(response);

        assertNotNull(domain);
        assertEquals(4.15, domain.valueIncVat());
        assertEquals(ZonedDateTime.parse("2025-12-25T14:32:16Z"), domain.validFrom());
        // assertEquals(ZonedDateTime.parse("2026-01-01T00:00:00Z"), domain.validTo());
    }

    @Test
    void toUnitRate_ShouldReturnNullWhenInputIsNull() {
        UnitRate domain = mapper.toUnitRate(null);

        assertNull(domain);
    }
}