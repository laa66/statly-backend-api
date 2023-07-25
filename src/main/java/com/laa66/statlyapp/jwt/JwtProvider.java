package com.laa66.statlyapp.jwt;

import com.laa66.statlyapp.model.OAuth2UserWrapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JwtProvider {
    private final SecretKey secretKey;

    public JwtProvider(String secret) {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String createToken(OAuth2UserWrapper principal) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + TimeUnit.MINUTES.toMillis(60));
        return Jwts.builder()
                .setSubject(Long.toString(principal.getUserId()))
                .setIssuer("statly")
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

}
