package uk.ac.soton.comp2300.group42.energyserver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

import java.time.ZoneId;

public class HouseMembershipMapperTest {

    private HouseMembershipMapper underTest;

    @BeforeEach
    void setUp() { underTest = Mappers.getMapper(HouseMembershipMapper.class); }

    @Test
    void shouldMapAllFieldsCorrectly() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setName("Test User");
        user.setEmail("testuser@abc.com");

        House house = new House();
        ReflectionTestUtils.setField(house, "id", 10L);
        house.setTimezone(ZoneId.of("Europe/London"));
        house.setAddress("Test House, Southampton");

        HouseMembership membership = new HouseMembership();
        membership.setUser(user);
        membership.setHouse(house);
        membership.setRole(Role.RESIDENT);
        membership.setHouseNickname("Test Nickname");

        HouseResponse houseResult = underTest.toHouseResponse(membership);
        HousemateResponse housemateResult = underTest.toHousemateResponse(membership);

        assertThat(houseResult).isNotNull();
        assertThat(houseResult.name()).isEqualTo("Test Nickname");
        assertThat(houseResult.address()).isEqualTo("Test House, Southampton");
        assertThat(houseResult.timezone()).isEqualTo(ZoneId.of("Europe/London"));
        assertThat(houseResult.role()).isEqualTo(Role.RESIDENT);

        assertThat(housemateResult).isNotNull();
        assertThat(housemateResult.userId()).isEqualTo(1L);
        assertThat(housemateResult.houseId()).isEqualTo(10L);
        assertThat(housemateResult.name()).isEqualTo("Test User");
        assertThat(housemateResult.email()).isEqualTo("testuser@abc.com");
        assertThat(housemateResult.role()).isEqualTo(Role.RESIDENT);

    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        HouseResponse houseResult = underTest.toHouseResponse(null);
        HousemateResponse housemateResponse = underTest.toHousemateResponse(null);

        assertThat(houseResult).isNull();
        assertThat(housemateResponse).isNull();
    }
}
