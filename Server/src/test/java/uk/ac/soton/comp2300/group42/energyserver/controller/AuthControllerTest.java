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
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtAuthFilter;
import uk.ac.soton.comp2300.group42.energyserver.service.AuthService;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final String BASE_URL = "/api/auth";

    @Test
    @DisplayName("POST /register - Should register user and return 200 OK with tokens")
    void register_ShouldReturn200() throws Exception {
        RegistrationRequest request = new RegistrationRequest("John Doe", "john@example.com", "SecurePass123!");
        AuthResponse response = new AuthResponse("access-token-123", "refresh-token-456");

        when(authService.register(any(RegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));

        verify(authService).register(any(RegistrationRequest.class));
    }

    @Test
    @DisplayName("POST /login - Should authenticate user and return 200 OK with tokens")
    void login_ShouldReturn200() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "SecurePass123!");
        AuthResponse response = new AuthResponse("access-token-123", "refresh-token-456");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /refresh - Should generate new tokens and return 200 OK")
    void refresh_ShouldReturn200() throws Exception {
        AuthResponse request = new AuthResponse("old-access", "valid-refresh-token");
        AuthResponse response = new AuthResponse("new-access-token", "new-refresh-token");

        when(authService.refresh(eq("valid-refresh-token"))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

        verify(authService).refresh(eq("valid-refresh-token"));
    }

    @Test
    @DisplayName("POST /logout - Should logout user and return 204 No Content")
    void logout_ShouldReturn204() throws Exception {
        AuthResponse request = new AuthResponse("access", "token-to-invalidate");

        doNothing().when(authService).logout(eq("token-to-invalidate"));

        mockMvc.perform(post(BASE_URL + "/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).logout(eq("token-to-invalidate"));
    }

    @Test
    @DisplayName("POST /logout-all - Should logout user from all devices and return 204 No Content")
    @WithMockUser
    void logoutAll_ShouldReturn204() throws Exception {
        doNothing().when(authService).logoutAll(any());

        mockMvc.perform(post(BASE_URL + "/logout-all")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}