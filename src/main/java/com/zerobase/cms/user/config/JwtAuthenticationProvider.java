package com.zerobase.cms.user.config;

import com.zerobase.cms.user.domain.common.UserVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtAuthenticationProvider {
    private final SecretKey secretKey;
    private final long tokenValidTime;

    public JwtAuthenticationProvider(
        @Value("${jwt.secret-key}") String secretKey,
        @Value("${jwt.token-expired-time-ms}") long tokenValidTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.tokenValidTime = tokenValidTime;
    }

    public String createToken(String userPk, Long id) {
        Date now = new Date();
        return Jwts.builder()
            .setSubject(userPk)
            .claim("id", id)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + tokenValidTime))
            .signWith(secretKey)
            .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public UserVo getUserVo(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return new UserVo(
            Long.valueOf(Objects.requireNonNull(claims.get("id")).toString()),
            claims.getSubject()
        );
    }
}
