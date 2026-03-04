package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Appliance;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplianceMapperTest {

    private ApplianceMapper underTest;

    @BeforeEach
    void setup() { underTest = Mappers.getMapper(ApplianceMapper.class); }

    @Test
    void shouldMapAllFieldsCorrectly() {

        House house = new House();
        ReflectionTestUtils.setField(house, "id", 1L);

        Appliance appliance = new Appliance();
        appliance.setHouse(house);
        appliance.setName("Test Appliance");

        ApplianceResponse result = underTest.toApplianceResponse(appliance);

        assertThat(result).isNotNull();
        assertThat(result.houseId()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Appliance");
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        ApplianceResponse result = underTest.toApplianceResponse(null);

        assertThat(result).isNull();
    }
}
