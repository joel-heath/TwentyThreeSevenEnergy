package uk.ac.soton.comp2300.group42.energyserver.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.energyserver.model.User;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private static final String SECRET = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());

    private JwtUtils jwtUtils;
    private User mockUser;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET);

        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(123L);
    }

    @Test
    void generateAccessToken_ShouldReturnValidTokenString() {
        String token = jwtUtils.generateAccessToken(mockUser);

        assertNotNull(token);
        assertFalse(token.trim().isEmpty());
        assertEquals(3, token.split("\\.").length); // (Header.Payload.Signature)
    }

    @Test
    void extractUserId_ShouldReturnCorrectId() {
        String token = jwtUtils.generateAccessToken(mockUser);
        Long extractedId = jwtUtils.extractUserId(token);

        assertEquals(123L, extractedId);
    }

    @Test
    void isTokenValid_WithCorrectUser_ShouldReturnTrue() {
        String token = jwtUtils.generateAccessToken(mockUser);

        boolean isValid = jwtUtils.isTokenValid(token, mockUser);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithWrongUser_ShouldReturnFalse() {
        String token = jwtUtils.generateAccessToken(mockUser);

        User wrongUser = mock(User.class);
        when(wrongUser.getId()).thenReturn(999L);

        boolean isValid = jwtUtils.isTokenValid(token, wrongUser);

        assertFalse(isValid);
    }

    @Test
    void tokenOperations_WithExpiredToken_ShouldThrowExpiredJwtException() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String expiredToken = Jwts.builder()
                .subject("vivianlushington1@hotmail.co.uk")
                .issuedAt(new Date(System.currentTimeMillis() - 10_000))
                .expiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(key)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtUtils.isTokenValid(expiredToken, mockUser));
        assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractUserId(expiredToken));
    }

    @Test
    void extractUserId_WithTamperedToken_ShouldThrowSignatureException() {
        String token = jwtUtils.generateAccessToken(mockUser);
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";

        assertThrows(SignatureException.class, () -> jwtUtils.extractUserId(tamperedToken));
    }

    @Test
    void extractUserId_WithMalformedToken_ShouldThrowMalformedJwtException() {
        String invalidToken = "this.is.not.a.valid.jwt";

        assertThrows(MalformedJwtException.class, () -> jwtUtils.extractUserId(invalidToken));
    }
}