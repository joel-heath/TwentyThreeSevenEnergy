package uk.ac.soton.comp2300.group42.energyserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.energyserver.exception.TokenRefreshException;
import uk.ac.soton.comp2300.group42.energyserver.model.RefreshToken;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.RefreshTokenRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.assignId;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User dummyUser;
    private RefreshToken dummyRefreshToken;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        dummyUser.setEmail("john@eight.twelve");

        dummyRefreshToken = new RefreshToken();
        ReflectionTestUtils.setField(dummyRefreshToken, "id", 100L);
        dummyRefreshToken.setUser(dummyUser);
        dummyRefreshToken.setToken("valid-token-string");
        dummyRefreshToken.setExpiryDate(Instant.now().plus(1, ChronoUnit.DAYS));
    }

    @Test
    void findByToken_Success() {
        when(refreshTokenRepository.findByToken("valid-token-string")).thenReturn(Optional.of(dummyRefreshToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken("valid-token-string");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(dummyRefreshToken);
    }

    @Test
    void createRefreshToken_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(rt -> assignId(rt, 200L));

        RefreshToken result = refreshTokenService.createRefreshToken(1L);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(result.getId()).isEqualTo(200L);
        assertThat(result.getUser()).isEqualTo(dummyUser);
        assertThat(result.getToken()).isNotBlank();
        assertThat(result.getExpiryDate()).isAfter(Instant.now());
    }

    @Test
    void createRefreshToken_UserNotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.createRefreshToken(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID 999 not found");

        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void verifyExpiration_TokenNotExpired_ReturnsToken() {
        RefreshToken result = refreshTokenService.verifyExpiration(dummyRefreshToken);

        assertThat(result).isEqualTo(dummyRefreshToken);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_TokenExpired_ThrowsExceptionAndDeletes() {
        dummyRefreshToken.setExpiryDate(Instant.now().minus(1, ChronoUnit.DAYS));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(dummyRefreshToken))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("Refresh token was expired");

        verify(refreshTokenRepository).delete(dummyRefreshToken);
    }

    @Test
    void deleteByUserId_UserExists_DeletesTokens() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));

        refreshTokenService.deleteByUserId(1L);

        verify(refreshTokenRepository).deleteByUser(dummyUser);
    }

    @Test
    void deleteByUserId_UserNotFound_DoesNothing() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        refreshTokenService.deleteByUserId(999L);

        verify(refreshTokenRepository, never()).deleteByUser(any());
    }

    @Test
    void deleteByToken_TokenExists_DeletesToken() {
        when(refreshTokenRepository.findByToken("valid-token-string")).thenReturn(Optional.of(dummyRefreshToken));

        refreshTokenService.deleteByToken("valid-token-string");

        verify(refreshTokenRepository).delete(dummyRefreshToken);
    }

    @Test
    void deleteByToken_TokenNotFound_DoesNothing() {
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        refreshTokenService.deleteByToken("invalid-token");

        verify(refreshTokenRepository, never()).delete(any());
    }
}