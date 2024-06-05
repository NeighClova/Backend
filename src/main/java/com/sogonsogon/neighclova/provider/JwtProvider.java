package com.sogonsogon.neighclova.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${JWT_SECRET}")
    private String JWT_SECRET;

    private Long JWT_EXPIRED_TIME = (long) (60*24*30);

    // jwt 생성
    public String create(String email) {
        Date expiredDate = Date.from(Instant.now().plus(JWT_EXPIRED_TIME, ChronoUnit.DAYS));

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        String jwt = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(email).setIssuedAt(new Date()).setExpiration(expiredDate)
                .compact();

        return jwt;
    }

    // jwt 검증
    public String validate(String jwt) {
        String subject = null;
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        try {
            subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        return subject;
    }
}
