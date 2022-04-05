package io.taxventures.service.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.taxventures.service.user.User;
import io.taxventures.service.user.UserPrincipal;
import io.taxventures.service.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

@Service
public class JWTTokenService implements TokenService {

    public JWTTokenService(@Value("${jwt-secret}") String JWT_SECRET) {
        this.JWT_SECRET = JWT_SECRET;
    }

    private final String JWT_SECRET;

    @Override
    public String generateToken(User user) {
        Instant expirationTime = Instant.now().plus(1, ChronoUnit.HOURS);
        Date expirationDate = Date.from(expirationTime);

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

        String compactTokenString = Jwts.builder()
                .claim("id", user.getId())
                .claim("sub", user.getUsername())
                .claim("role", user.getUserRole().name())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return "Bearer " + compactTokenString;
    }

    @Override
    public UserPrincipal parseToken(String token) {
        byte[] secretBytes  = JWT_SECRET.getBytes();
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretBytes)
                .build()
                .parseClaimsJws(token);

        String username = jwsClaims.getBody().getSubject();
        String userId = jwsClaims.getBody().get("id", String.class);
        String role = jwsClaims.getBody().get("role", String.class);
        boolean isAdmin = Objects.equals(role, UserRole.ADMIN.name());

        return new UserPrincipal(userId, username, isAdmin);
    }
}
