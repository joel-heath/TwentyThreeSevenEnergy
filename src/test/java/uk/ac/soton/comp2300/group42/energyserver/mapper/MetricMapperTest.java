package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Metric;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MetricMapperTest {

    private MetricMapper underTest;

    @BeforeEach
    void setUp() {
        underTest = Mappers.getMapper(MetricMapper.class);
    }

    @Test
    void shouldMapAllFieldsCorrectly() {
        House house = new House();
        ReflectionTestUtils.setField(house, "id", 1L);

        Metric metric = new Metric();
        metric.setEnergyUsed(50.0);
        metric.setDate(LocalDate.of(2026, 3, 4));
        metric.setHouse(house);

        MetricResponse result = underTest.toMetricResponse(metric);

        assertThat(result).isNotNull();
        assertThat(result.houseId()).isEqualTo(1L);
        assertThat(result.date()).isEqualTo(LocalDate.of(2026, 3, 4));
        assertThat(result.energyUsed()).isEqualTo(50.0);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        MetricResponse result = underTest.toMetricResponse(null);

        assertThat(result).isNull();
    }
}
