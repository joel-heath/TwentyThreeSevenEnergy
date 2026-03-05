package uk.ac.soton.comp2300.group42.energyserver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyserver.model.Activation;
import uk.ac.soton.comp2300.group42.energyserver.model.Appliance;
import uk.ac.soton.comp2300.group42.energyserver.model.House;

import java.time.LocalDate;
import java.time.LocalTime;


class ActivationMapperTest {

    private ActivationMapper underTest;

    @BeforeEach
    void setUp() {
        underTest = Mappers.getMapper(ActivationMapper.class);
    }

    @Test
    void shouldMapAllFieldsCorrectly() {
        House house = new House();
        ReflectionTestUtils.setField(house, "id", 1L);

        Appliance appliance = new Appliance();
        ReflectionTestUtils.setField(appliance, "id", 10L);
        appliance.setHouse(house);

        Activation activation = new Activation();
        activation.setAppliance(appliance);
        activation.setType(ActivationType.NON_RECURRING);
        activation.setActivationTime(LocalTime.of(8, 0));
        activation.setActivationDate(LocalDate.of(2025, 12, 25));
        activation.setRecursMonday(true);
        activation.setRecursTuesday(false);
        activation.setRecursWednesday(true);
        activation.setRecursThursday(false);
        activation.setRecursFriday(false);
        activation.setRecursSaturday(true);
        activation.setRecursSunday(false);

        ActivationResponse result = underTest.toActivationResponse(activation);

        assertThat(result).isNotNull();
        assertThat(result.houseId()).isEqualTo(1L);
        assertThat(result.applianceId()).isEqualTo(10L);
        assertThat(result.type()).isEqualTo(ActivationType.NON_RECURRING);
        assertThat(result.activationTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.activationDate()).isEqualTo(LocalDate.of(2025, 12, 25));
        assertThat(result.recursMonday()).isEqualTo(true);
        assertThat(result.recursTuesday()).isEqualTo(false);
        assertThat(result.recursWednesday()).isEqualTo(true);
        assertThat(result.recursThursday()).isEqualTo(false);
        assertThat(result.recursFriday()).isEqualTo(false);
        assertThat(result.recursSaturday()).isEqualTo(true);
        assertThat(result.recursSunday()).isEqualTo(false);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        ActivationResponse result = underTest.toActivationResponse(null);

        assertThat(result).isNull();
    }
}