package com.dtbonthego.common.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * Utility class for JWT token operations.
 * This is a common implementation used across all microservices.
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * Generates a JWT token for a user.
     *
     * @param authentication the authentication object containing the principal
     * @param roles the roles assigned to the user
     * @return the generated JWT token
     */
    public String generateJwtToken(Authentication authentication, List<String> roles) {
        Long userPrincipal = (Long) authentication.getPrincipal();

        logger.info("Generate token for user ID: {}", userPrincipal);
        return Jwts.builder()
                .setSubject(userPrincipal.toString())
                .setIssuedAt(new Date())
                .claim("roles", roles)
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Gets the profile ID from a JWT token.
     *
     * @param token the JWT token
     * @return the profile ID
     */
    public Long getProfileIdFromJwtToken(String token) {
        return Long.valueOf(Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject());
    }

    /**
     * Validates a JWT token.
     *
     * @param authToken the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
    
    /**
     * Gets the signing key for JWT operations.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
    
    /**
     * Extracts claims from JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
