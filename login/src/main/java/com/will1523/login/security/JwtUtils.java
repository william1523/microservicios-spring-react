package com.will1523.login.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // Must be at least 256 bits (32 bytes)
    // In production, this should be in an environment variable
    private static final String SECRET = "YourSuperSecretKeyForJwtSigningMustBe32BytesOrMore!";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public String generateToken(com.will1523.login.model.User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .claim("companyCode", user.getCompanyCode())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
}
