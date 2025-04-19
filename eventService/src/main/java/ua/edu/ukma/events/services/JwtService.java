package ua.edu.ukma.events.services;

import java.util.*;
import java.util.function.Function;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

@Component
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getRole(String token) {
        Claims claims = extractAllClaims(token);
        Object role = claims.get("role");
        return role.toString();
    }

    public UUID getUserId(String token) {
        Claims claims = extractAllClaims(token);
        UUID role = UUID.fromString(claims.get("id", String.class));
        return role;
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        return List.of(new SimpleGrantedAuthority(getRole(token)));
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }
}
