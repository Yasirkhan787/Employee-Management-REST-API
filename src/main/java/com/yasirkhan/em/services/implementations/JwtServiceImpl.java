package com.yasirkhan.em.services.implementations;

import com.yasirkhan.em.exceptions.ResourceNotFoundException;
import com.yasirkhan.em.exceptions.TokenExpiredException;
import com.yasirkhan.em.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final String SECRET_KEY;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtServiceImpl(@Value("${jwt.secret}") String secretKey, UserDetailsServiceImpl userDetailsService) {
        SECRET_KEY = secretKey;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String generateJwtToken(String username, Map<String, Object> claims) {
        long EXPIRATION_TIME = 1000 * 60 * 60L; // 1 hour
        String userId = String.valueOf(claims.get("userId"));

        return Jwts
                .builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", claims.get("role"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // If the token is expired, extractUsername() triggers extractAllClaims(),which will instantly throw our custom TokenExpiredException.
        // If it is NOT expired, we safely get the username and verify it against the DB.
        String username = extractUsername(token);

        if (!username.equals(userDetails.getUsername())) {
            throw new ResourceNotFoundException("User Not Found with username: " + username);
        }

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    // --- Helper Methods ---

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token has expired");
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Helper method to convert our String into a cryptographic SecretKey object </br>
     * Convert the Java String into a basic array of raw bytes (1s and 0s).
     * We specify UTF-8 to ensure special characters are read correctly.</br>
     * Decode the Base64 bytes back into the original, raw secret bytes.</br>
     * WARNING: This assumes your application.properties key is already Base64 encoded!</br>
     * Example valid key: "emQ4dkszIXBMOSNtUTJAd1g1KnlUN15uUjQmYkoxJWM="</br>
     * Wrap those raw decoded bytes into a secure HMAC cryptographic object.</br>
     * The library also checks here to ensure the byte array is at least 256 bits long.</br>
     */
    private SecretKey getSigningKey() {
        byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] decodedSecretKey = Base64.getDecoder().decode(secretKeyBytes);
        return Keys.hmacShaKeyFor(decodedSecretKey);
    }
}