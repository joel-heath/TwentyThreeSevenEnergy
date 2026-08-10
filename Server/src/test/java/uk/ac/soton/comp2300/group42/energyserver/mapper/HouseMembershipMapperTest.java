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

    private HouseMembership dummyMembership;

    @BeforeEach
    void setUp() {
        underTest = Mappers.getMapper(HouseMembershipMapper.class);

        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        dummyUser.setName("Test User");
        dummyUser.setEmail("testuser@abc.com");

        House dummyHouse = new House();
        ReflectionTestUtils.setField(dummyHouse, "id", 10L);
        dummyHouse.setTimezone(ZoneId.of("Europe/London"));
        dummyHouse.setAddress("Test House, Southampton");

        dummyMembership = new HouseMembership();
        dummyMembership.setUser(dummyUser);
        dummyMembership.setHouse(dummyHouse);
        dummyMembership.setRole(Role.RESIDENT);
        dummyMembership.setHouseNickname("Test Nickname");
    }

    @Test
    void toHouseResponse_ShouldMapAllFieldsCorrectly() {
        HouseResponse houseResult = underTest.toHouseResponse(dummyMembership);

        assertThat(houseResult).isNotNull();
        assertThat(houseResult.name()).isEqualTo("Test Nickname");
        assertThat(houseResult.address()).isEqualTo("Test House, Southampton");
        assertThat(houseResult.timezone()).isEqualTo(ZoneId.of("Europe/London"));
        assertThat(houseResult.role()).isEqualTo(Role.RESIDENT);
    }

    @Test
    void toHousemateResponse_ShouldMapAllFieldsCorrectly() {
        HousemateResponse housemateResult = underTest.toHousemateResponse(dummyMembership);

        assertThat(housemateResult).isNotNull();
        assertThat(housemateResult.userId()).isEqualTo(1L);
        assertThat(housemateResult.houseId()).isEqualTo(10L);
        assertThat(housemateResult.name()).isEqualTo("Test User");
        assertThat(housemateResult.email()).isEqualTo("testuser@abc.com");
        assertThat(housemateResult.role()).isEqualTo(Role.RESIDENT);
    }

    @Test
    void toHouseResponse_ShouldReturnNullWhenInputIsNull() {
        HouseResponse houseResult = underTest.toHouseResponse(null);

        assertThat(houseResult).isNull();
    }

    @Test
    void toHousemateResponse_ShouldReturnNullWhenInputIsNull() {
        HousemateResponse housemateResponse = underTest.toHousemateResponse(null);

        assertThat(housemateResponse).isNull();
    }
}
