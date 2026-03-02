package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class HouseMapperTest {

    private final HouseMapper mapper = Mappers.getMapper(HouseMapper.class);

    @Test
    void toHouse_ShouldMapAllFieldsCorrectly() {
        HouseResponse response = new HouseResponse(
                1L,
                "Primary House",
                "123 Main St",
                ZoneId.of("Europe/London"),
                Role.RESIDENT
        );

        House domain = mapper.toHouse(response);

        assertNotNull(domain);
        assertEquals(1L, domain.id());
        assertEquals("Primary House", domain.name());
        assertEquals("123 Main St", domain.address());
        assertEquals(ZoneId.of("Europe/London"), domain.timezone());
        assertEquals(Role.RESIDENT, domain.role());
    }

    @Test
    void toHouse_ShouldReturnNullWhenInputIsNull() {
        House domain = mapper.toHouse(null);

        assertNull(domain);
    }

    @Test
    void toCreateHouseRequest_ShouldMapFieldsAndDropIdAndRole() {
        House domain = new House(
                1L,
                "Primary House",
                "123 Main St",
                ZoneId.of("Europe/London"),
                Role.RESIDENT
        );

        CreateHouseRequest request = mapper.toCreateHouseRequest(domain);

        assertNotNull(request);
        assertEquals("Primary House", request.name());
        assertEquals("123 Main St", request.address());
        assertEquals(ZoneId.of("Europe/London"), request.timezone());
    }

    @Test
    void toCreateHouseRequest_ShouldReturnNullWhenInputIsNull() {
        CreateHouseRequest request = mapper.toCreateHouseRequest(null);

        assertNull(request);
    }

    @Test
    void toUpdateHouseRequest_ShouldMapFieldsAndDropIdAndRol() {
        House domain = new House(
                1L,
                "Primary House",
                "123 Main St",
                ZoneId.of("Europe/London"),
                Role.RESIDENT
        );

        UpdateHouseRequest request = mapper.toUpdateHouseRequest(domain);

        assertNotNull(request);
        assertEquals("Primary House", request.name());
        assertEquals("123 Main St", request.address());
        assertEquals(ZoneId.of("Europe/London"), request.timezone());
    }

    @Test
    void toUpdateHouseRequest_ShouldReturnNullWhenInputIsNull() {
        UpdateHouseRequest request = mapper.toUpdateHouseRequest(null);

        assertNull(request);
    }
}