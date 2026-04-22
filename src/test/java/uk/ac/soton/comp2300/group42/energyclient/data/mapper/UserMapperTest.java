package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.preferences.*;
import uk.ac.soton.comp2300.group42.user.UpdateUserRequest;
import uk.ac.soton.comp2300.group42.user.UserResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUser_ShouldMapAllFieldsCorrectly() {
        UserResponse response = new UserResponse(1L, "John Doe", "johndoe@example.com");

        User domain = mapper.toUser(response);

        assertNotNull(domain);
        assertEquals(1L, domain.id());
        assertEquals("John Doe", domain.name());
        assertEquals("johndoe@example.com", domain.email());
    }

    @Test
    void toUser_ShouldReturnNullWhenInputIsNull() {
        User domain = mapper.toUser(null);

        assertNull(domain);
    }

    @Test
    void toPreferences_ShouldMapAllFieldsCorrectly() {
        PreferencesResponse preferencesResponse = new PreferencesResponse(
                1L, false, ColorVision.TYPICAL, Theme.LIGHT, Mode.SIMPLE, true, 5.0, 2L
        );

        Preferences domain = mapper.toPreferences(preferencesResponse);

        assertNotNull(domain);
        assertEquals(1L, domain.userId());
        assertFalse(domain.largeFont());
        assertEquals(ColorVision.TYPICAL, domain.vision());
        assertEquals(Theme.LIGHT, domain.theme());
        assertEquals(Mode.SIMPLE, domain.mode());
        assertTrue(domain.shareLocation());
        assertEquals(5.0, domain.energyGoal());
        assertEquals(2L, domain.activeHouseId());
    }

    @Test
    void toPreferences_ShouldReturnNullWhenInputIsNull() {
        Preferences domain = mapper.toPreferences(null);

        assertNull(domain);
    }

    @Test
    void toUpdateUserRequest_ShouldMapFieldsAndDropId() {
        User user = new User(1L, "Jane Doe", "janedoe@example.com");

        UpdateUserRequest request = mapper.toUpdateUserRequest(user);

        assertNotNull(request);
        assertEquals("Jane Doe", request.name());
        assertEquals("janedoe@example.com", request.email());
    }

    @Test
    void toUpdateUserRequest_ShouldReturnNullWhenInputIsNull() {
        UpdateUserRequest request = mapper.toUpdateUserRequest(null);

        assertNull(request);
    }

    @Test
    void toUpdatePreferencesRequest_ShouldMapFieldsAndDropUserId() {
        Preferences preferences = new Preferences(
                1L, true, ColorVision.TRITAN, Theme.DARK, Mode.ADVANCED, false, 10.0, 10L
        );

        UpdatePreferencesRequest request = mapper.toUpdatePreferencesRequest(preferences);

        assertNotNull(request);
        assertTrue(request.largeFont());
        assertEquals(ColorVision.TRITAN, request.vision());
        assertEquals(Theme.DARK, request.theme());
        assertEquals(Mode.ADVANCED, request.mode());
        assertFalse(request.shareLocation());
        assertEquals(10.0, request.energyGoal());
        assertEquals(10L, request.activeHouseId());
    }

    @Test
    void toUpdatePreferencesRequest_ShouldReturnNullWhenInputIsNull() {
        UpdatePreferencesRequest request = mapper.toUpdatePreferencesRequest(null);

        assertNull(request);
    }
}