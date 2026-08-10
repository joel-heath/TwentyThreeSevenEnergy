package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MetricMapperTest {

    private final MetricMapper mapper = Mappers.getMapper(MetricMapper.class);

    @Test
    void toMetric_ShouldMapAllFieldsCorrectly() {
        MetricResponse response = new MetricResponse(
                10L,
                1L,
                LocalDateTime.of(2025, 12, 25, 13, 0),
                4.15,
                6.78,
                EnergyCategory.GAS
        );

        Metric domain = mapper.toMetric(response);

        assertNotNull(domain);
        assertEquals(10L, domain.id());
        assertEquals(1L, domain.houseId());
        assertEquals(LocalDateTime.of(2025, 12, 25, 13, 0), domain.dateTime());
        assertEquals(4.15, domain.energyUsed());
        assertEquals(6.78, domain.energyPrice());
        assertEquals(EnergyCategory.GAS, domain.category());
    }

    @Test
    void toMetric_ShouldReturnNullWhenInputIsNull() {
        Metric domain = mapper.toMetric(null);

        assertNull(domain);
    }
}