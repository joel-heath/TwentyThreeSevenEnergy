package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtAuthFilter;
import uk.ac.soton.comp2300.group42.energyserver.service.AuthService;
import uk.ac.soton.comp2300.group42.energyserver.service.UserService;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.preferences.UpdatePreferencesRequest;
import uk.ac.soton.comp2300.group42.user.ChangePasswordRequest;
import uk.ac.soton.comp2300.group42.user.DeleteUserRequest;
import uk.ac.soton.comp2300.group42.user.UpdateUserRequest;
import uk.ac.soton.comp2300.group42.user.UserResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final Long USER_ID = 1L;
    private final String BASE_URL = "/api/users";

    @Test
    @DisplayName("GET / - Should return all users and 200 OK")
    @WithMockUser
    void getAllUsers_ShouldReturn200() throws Exception {
        List<UserResponse> responses = List.of(
                new UserResponse(USER_ID, "Alice", "alice@example.com"),
                new UserResponse(2L, "Bob", "bob@example.com")
        );

        when(userService.findAll()).thenReturn(responses);

        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));

        verify(userService).findAll();
    }

    @Test
    @DisplayName("GET /me - Should return current user and 200 OK")
    @WithMockUser
    void getCurrentUser_ShouldReturn200() throws Exception {
        UserResponse response = new UserResponse(USER_ID, "Alice", "alice@example.com");

        when(userService.getCurrentUser(any())).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(userService).getCurrentUser(any());
    }

    @Test
    @DisplayName("PUT /me - Should update current user and return 200 OK")
    @WithMockUser
    void updateCurrentUser_ShouldReturn200() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Alice Updated", "alice.new@example.com");
        UserResponse response = new UserResponse(USER_ID, "Alice Updated", "alice.new@example.com");

        when(userService.updateCurrentUser(any(), any(UpdateUserRequest.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice.new@example.com"));

        verify(userService).updateCurrentUser(any(), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("DELETE /me - Should delete current user and return 204 No Content")
    @WithMockUser
    void deleteCurrentUser_ShouldReturn204() throws Exception {
        DeleteUserRequest request = new DeleteUserRequest("MySecurePassword123");

        doNothing().when(userService).deleteCurrentUser(any(), any(DeleteUserRequest.class));

        mockMvc.perform(delete(BASE_URL + "/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).deleteCurrentUser(any(), any(DeleteUserRequest.class));
    }

    @Test
    @DisplayName("PUT /me/password - Should change password and return 204 No Content")
    @WithMockUser
    void changeCurrentUserPassword_ShouldReturn204() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("OldPass", "NewPass");

        doNothing().when(authService).changePassword(any(), any(ChangePasswordRequest.class));

        mockMvc.perform(put(BASE_URL + "/me/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).changePassword(any(), any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("GET /me/preferences - Should return preferences and 200 OK")
    @WithMockUser
    void getCurrentUserPreferences_ShouldReturn200() throws Exception {
        PreferencesResponse response = new PreferencesResponse(
                USER_ID, true, ColorVision.TYPICAL, Theme.DARK, Mode.ADVANCED, true, 150.0, 100L
        );

        when(userService.getCurrentUserPreferences(any())).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/me/preferences")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.largeFont").value(true))
                .andExpect(jsonPath("$.vision").value("typical"))
                .andExpect(jsonPath("$.theme").value("dark"))
                .andExpect(jsonPath("$.mode").value("advanced"))
                .andExpect(jsonPath("$.shareLocation").value(true))
                .andExpect(jsonPath("$.energyGoal").value(150.0))
                .andExpect(jsonPath("$.activeHouseId").value(100L));

        verify(userService).getCurrentUserPreferences(any());
    }

    @Test
    @DisplayName("PUT /me/preferences - Should update preferences and return 200 OK")
    @WithMockUser
    void updateCurrentUserPreferences_ShouldReturn200() throws Exception {
        UpdatePreferencesRequest request = new UpdatePreferencesRequest(
                USER_ID, false, ColorVision.PROTAN, Theme.LIGHT_CONTRAST, Mode.SIMPLE, true, 200.0, 101L
        );
        PreferencesResponse response = new PreferencesResponse(
                USER_ID, false, ColorVision.PROTAN, Theme.LIGHT_CONTRAST, Mode.SIMPLE, true, 200.0, 101L
        );

        when(userService.updateCurrentUserPreferences(any(), any(UpdatePreferencesRequest.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/me/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.largeFont").value(false))
                .andExpect(jsonPath("$.vision").value("protanopia"))
                .andExpect(jsonPath("$.theme").value("light_high_contrast"))
                .andExpect(jsonPath("$.mode").value("simple"))
                .andExpect(jsonPath("$.shareLocation").value(true))
                .andExpect(jsonPath("$.energyGoal").value(200.0))
                .andExpect(jsonPath("$.activeHouseId").value(101L));

        verify(userService).updateCurrentUserPreferences(any(), any(UpdatePreferencesRequest.class));
    }
}