package uk.ac.soton.comp2300.group42.energyserver.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;
import uk.ac.soton.comp2300.group42.energyserver.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    // We'll create an actual secret in application.properties for production
    private static final String SECRET = "5481ca00242289316679fc0ed90ee7eb3a635151cd22779a0a67f334f8e8208e";

    private static final long ACCESS_EXPIRATION = 1000 * 60 * 15;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JwtUtils.ACCESS_EXPIRATION))
                .signWith(getSignKey())
                .compact();
    }

    public boolean isTokenValid(String token, User user) {
        final Long extractedUserId = extractUserId(token);
        return (extractedUserId.equals(user.getId()) && !isTokenExpired(token));
    }

    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return Long.parseLong(subject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
