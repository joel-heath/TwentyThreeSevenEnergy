package uk.ac.soton.comp2300.group42.energyserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.InvalidCredentialsException;
import uk.ac.soton.comp2300.group42.energyserver.exception.TokenRefreshException;
import uk.ac.soton.comp2300.group42.energyserver.exception.UserAlreadyExistsException;
import uk.ac.soton.comp2300.group42.energyserver.model.*;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.PreferencesRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtUtils;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.ChangePasswordRequest;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;

import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.assignId;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.verifySaveAndCapture;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HouseRepository houseRepo;

    @Mock
    private HouseMembershipRepository membershipRepo;

    @Mock
    private PreferencesRepository preferencesRepo;

    @InjectMocks
    private AuthService authService;

    private User dummyUser;
    private RefreshToken dummyRefreshToken;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        dummyUser.setEmail("test@example.com");
        dummyUser.setName("Test User");
        dummyUser.setPassword("encoded_password");

        dummyRefreshToken = new RefreshToken();
        ReflectionTestUtils.setField(dummyRefreshToken, "id", 100L);
        dummyRefreshToken.setToken("dummy-refresh-token");
        dummyRefreshToken.setUser(dummyUser);
    }

    @Test
    void register_Success() {
        RegistrationRequest request = new RegistrationRequest("New User", "new@example.com", "password123");

        when(userRepo.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");
        
        when(userRepo.save(any(User.class))).thenAnswer(u -> assignId(u, 1L));
        when(houseRepo.save(any(House.class))).thenAnswer(h -> assignId(h, 10L));
        when(membershipRepo.save(any(HouseMembership.class))).thenAnswer(m -> assignId(m, 100L));
        when(preferencesRepo.save(any(Preferences.class))).thenAnswer(p -> assignId(p, 1000L));

        when(jwtUtils.generateAccessToken(any(User.class))).thenReturn("dummy-access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(dummyRefreshToken);

        AuthResponse result = authService.register(request);

        User savedUser = verifySaveAndCapture(userRepo, User.class);
        House savedHouse = verifySaveAndCapture(houseRepo, House.class);
        HouseMembership savedMembership = verifySaveAndCapture(membershipRepo, HouseMembership.class);
        Preferences savedPrefs = verifySaveAndCapture(preferencesRepo, Preferences.class);

        assertThat(savedUser.getId()).isEqualTo(1L);
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getPassword()).isEqualTo("hashed_password");

        assertThat(savedHouse.getId()).isEqualTo(10L);
        assertThat(savedHouse.getAddress()).isEqualTo("No address set");
        assertThat(savedHouse.getTimezone()).isEqualTo(ZoneId.of("UTC"));

        assertThat(savedMembership.getId()).isEqualTo(100L);
        assertThat(savedMembership.getUser()).isEqualTo(savedUser);
        assertThat(savedMembership.getHouse()).isEqualTo(savedHouse);
        assertThat(savedMembership.getRole()).isEqualTo(Role.OWNER);
        assertThat(savedMembership.getHouseNickname()).isEqualTo("Primary House");

        assertThat(savedPrefs.getId()).isEqualTo(1000L);
        assertThat(savedPrefs.getUser()).isEqualTo(savedUser);
        assertThat(savedPrefs.getActiveHouse()).isEqualTo(savedHouse);
        assertThat(savedPrefs.getTheme()).isEqualTo(Theme.LIGHT);
        assertThat(savedPrefs.getMode()).isEqualTo(Mode.SIMPLE);
        assertThat(savedPrefs.getColorVision()).isEqualTo(ColorVision.TYPICAL);
        assertThat(savedPrefs.getLargeFont()).isFalse();
        assertThat(savedPrefs.getShareLocation()).isFalse();

        assertThat(result.accessToken()).isEqualTo("dummy-access-token");
        assertThat(result.refreshToken()).isEqualTo("dummy-refresh-token");
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        RegistrationRequest request = new RegistrationRequest("Test User", "test@example.com", "password");

        when(userRepo.findByEmail(request.email())).thenReturn(Optional.of(dummyUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepo, never()).save(any());
        verifyNoInteractions(houseRepo, membershipRepo, preferencesRepo, jwtUtils, refreshTokenService);
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(userRepo.findByEmail(request.email())).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches(request.password(), dummyUser.getPassword())).thenReturn(true);
        when(jwtUtils.generateAccessToken(dummyUser)).thenReturn("new-access-token");
        when(refreshTokenService.createRefreshToken(dummyUser.getId())).thenReturn(dummyRefreshToken);

        AuthResponse result = authService.login(request);

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("dummy-refresh-token");
    }

    @Test
    void login_InvalidEmail_ThrowsException() {
        LoginRequest request = new LoginRequest("wrong@example.com", "password123");

        when(userRepo.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verifyNoInteractions(jwtUtils, refreshTokenService);
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest("test@example.com", "wrong-password");

        when(userRepo.findByEmail(request.email())).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches(request.password(), dummyUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verifyNoInteractions(jwtUtils, refreshTokenService);
    }

    @Test
    void refresh_Success() {
        String oldRefreshToken = "valid-old-token";
        
        when(refreshTokenService.findByToken(oldRefreshToken)).thenReturn(Optional.of(dummyRefreshToken));
        when(refreshTokenService.verifyExpiration(dummyRefreshToken)).thenReturn(dummyRefreshToken);
        when(jwtUtils.generateAccessToken(dummyUser)).thenReturn("fresh-access-token");

        AuthResponse result = authService.refresh(oldRefreshToken);

        assertThat(result.accessToken()).isEqualTo("fresh-access-token");
        assertThat(result.refreshToken()).isEqualTo(oldRefreshToken);
    }

    @Test
    void refresh_InvalidToken_ThrowsException() {
        String invalidToken = "invalid-token";

        when(refreshTokenService.findByToken(invalidToken)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(invalidToken))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("Refresh token is not in database");

        verifyNoInteractions(jwtUtils);
    }

    @Test
    void refresh_ExpiredToken_ThrowsException() {
        String expiredToken = "expired-token";

        when(refreshTokenService.findByToken(expiredToken)).thenReturn(Optional.of(dummyRefreshToken));
        when(refreshTokenService.verifyExpiration(dummyRefreshToken)).thenThrow(new TokenRefreshException("Refresh token was expired"));

        assertThatThrownBy(() -> authService.refresh(expiredToken))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("Refresh token was expired");

        verifyNoInteractions(jwtUtils);
    }

    @Test
    void logout_Success() {
        String token = "token-to-delete";
        authService.logout(token);
        verify(refreshTokenService).deleteByToken(token);
    }

    @Test
    void logoutAll_Success() {
        Long userId = 1L;
        authService.logoutAll(userId);
        verify(refreshTokenService).deleteByUserId(userId);
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest("current-password", "new-secure-password");

        when(passwordEncoder.matches(request.oldPassword(), dummyUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.newPassword())).thenReturn("encoded_new_password");

        authService.changePassword(dummyUser, request);

        verify(passwordEncoder).encode(request.newPassword());
        assertThat(dummyUser.getPassword()).isEqualTo("encoded_new_password");
    }

    @Test
    void changePassword_InvalidCurrentPassword_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrong-current-password", "new-password");

        when(passwordEncoder.matches(request.oldPassword(), dummyUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(dummyUser, request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(passwordEncoder, never()).encode(any());
        assertThat(dummyUser.getPassword()).isEqualTo("encoded_password");
    }

    @Test
    void verifyPassword_Success() {
        String password = "current-password";

        when(passwordEncoder.matches(password, dummyUser.getPassword())).thenReturn(true);

        authService.verifyPassword(dummyUser, password, "Error message");

        verify(passwordEncoder).matches(password, dummyUser.getPassword());
    }

    @Test
    void verifyPassword_InvalidPassword_ThrowsException() {
        String password = "wrong-password";

        when(passwordEncoder.matches(password, dummyUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.verifyPassword(dummyUser, password, "Error message"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Error message");

        verify(passwordEncoder).matches(password, dummyUser.getPassword());

    }
}