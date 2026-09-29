package org.lab.kpoproject.utils.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.lab.kpoproject.entity.Role;
import org.lab.kpoproject.entity.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    private final Logger logger = LoggerFactory.getLogger("custom");

    @Value("#{${jwt.accessTokenLifetime}*60*1000}")
    private Long accessTokenLifetime;
    @Value("#{${jwt.refreshTokenLifetime}*60*1000}")
    private Long refreshTokenLifetime;
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(
            final String email,
            final Role role,
            final TypeToken typeToken) {

        final long now = System.currentTimeMillis();
        final long lifetime = switch (typeToken) {
            case ACCESS -> accessTokenLifetime;
            case REFRESH -> refreshTokenLifetime;
        };

        return Jwts.builder()
                .subject(email)
                .claim("role", role.getRole())
                .claim("type", typeToken.name())
                .issuedAt(new Date(now))
                .expiration(new Date(now + lifetime))
                .signWith(getSecretKey())
                .compact();
    }

    public boolean validateToken(final String token)
            throws ExpiredJwtException,
            UnsupportedJwtException,
            MalformedJwtException,
            io.jsonwebtoken.security.SecurityException {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token");
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token");
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token");
            throw e;
        } catch (SecurityException e) {
            logger.error("Security exception");
            throw e;
        }
    }

    public String getTypeToken(final String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("type").toString();
    }

    public String getEmail(final String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date getExpirationDateFromToken(final String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    private SecretKey getSecretKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
