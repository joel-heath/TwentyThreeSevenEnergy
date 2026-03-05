package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.user.UserResponse;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper underTest;

    @BeforeEach
    void setUp() {
        underTest = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void shouldMapAllFieldsCorrectly() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@abc.com");

        UserResponse result = underTest.toUserResponse(user);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test User");
        assertThat(result.email()).isEqualTo("testuser@abc.com");
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        UserResponse result = underTest.toUserResponse(null);

        assertThat(result).isNull();
    }
}
