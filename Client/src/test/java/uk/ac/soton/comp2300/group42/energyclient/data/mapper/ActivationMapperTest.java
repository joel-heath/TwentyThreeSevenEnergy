package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.activation.CreateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.UpdateActivationRequest;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivationMapperTest {

    private final ActivationMapper mapper = Mappers.getMapper(ActivationMapper.class);

    @Test
    void toActivation_ShouldMapAllFieldsCorrectly() {
        ActivationResponse response = new ActivationResponse(
                100L,
                10L,
                1L,
                ActivationType.RECURRING,
                LocalTime.of(15, 30),
                LocalDate.of(2025, 12, 25),
                true, true, false, false, true, false, true
        );

        Activation domain = mapper.toActivation(response);

        assertNotNull(domain);
        assertEquals(100L, domain.id());
        assertEquals(10L, domain.applianceId());
        assertEquals(1L, domain.houseId());
        assertEquals(ActivationType.RECURRING, domain.type());
        assertEquals(LocalTime.of(15, 30), domain.activationTime());
        assertEquals(LocalDate.of(2025, 12, 25), domain.activationDate());
        assertTrue(domain.recursMonday());
        assertTrue(domain.recursTuesday());
        assertFalse(domain.recursWednesday());
        assertFalse(domain.recursThursday());
        assertTrue(domain.recursFriday());
        assertFalse(domain.recursSaturday());
        assertTrue(domain.recursSunday());
    }

    @Test
    void toActivation_ShouldReturnNullWhenInputIsNull() {
        Activation domain = mapper.toActivation(null);

        assertNull(domain);
    }

    @Test
    void toCreateActivationRequest_ShouldMapFieldsAndDropId() {
        Activation domain = new Activation(
                100L,
                10L,
                1L,
                ActivationType.RECURRING,
                LocalTime.of(15, 30),
                LocalDate.of(2025, 12, 25),
                true, true, false, false, true, false, true
        );

        CreateActivationRequest request = mapper.toCreateActivationRequest(domain);

        assertNotNull(request);
        assertEquals(10L, request.applianceId());
        assertEquals(1L, request.houseId());
        assertEquals(ActivationType.RECURRING, request.type());
        assertEquals(LocalTime.of(15, 30), request.activationTime());
        assertEquals(LocalDate.of(2025, 12, 25), request.activationDate());
        assertTrue(request.recursMonday());
        assertTrue(request.recursTuesday());
        assertFalse(request.recursWednesday());
        assertFalse(request.recursThursday());
        assertTrue(request.recursFriday());
        assertFalse(request.recursSaturday());
        assertTrue(request.recursSunday());
    }

    @Test
    void toCreateActivationRequest_ShouldReturnNullWhenInputIsNull() {
        CreateActivationRequest request = mapper.toCreateActivationRequest(null);

        assertNull(request);
    }

    @Test
    void toUpdateActivationRequest_ShouldMapFieldsAndDropId() {
        Activation domain = new Activation(
                100L,
                10L,
                1L,
                ActivationType.RECURRING,
                LocalTime.of(15, 30),
                LocalDate.of(2025, 12, 25),
                true, true, false, false, true, false, true
        );

        UpdateActivationRequest request = mapper.toUpdateActivationRequest(domain);

        assertNotNull(request);
        assertEquals(10L, request.applianceId());
        assertEquals(1L, request.houseId());
        assertEquals(ActivationType.RECURRING, request.type());
        assertEquals(LocalTime.of(15, 30), request.activationTime());
        assertEquals(LocalDate.of(2025, 12, 25), request.activationDate());
        assertTrue(request.recursMonday());
        assertTrue(request.recursTuesday());
        assertFalse(request.recursWednesday());
        assertFalse(request.recursThursday());
        assertTrue(request.recursFriday());
        assertFalse(request.recursSaturday());
        assertTrue(request.recursSunday());
    }

    @Test
    void toUpdateActivationRequest_ShouldReturnNullWhenInputIsNull() {
        UpdateActivationRequest request = mapper.toUpdateActivationRequest(null);

        assertNull(request);
    }
}