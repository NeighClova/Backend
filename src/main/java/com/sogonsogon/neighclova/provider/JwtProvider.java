package com.sogonsogon.neighclova.provider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${JWT_SECRET}")
    private String JWT_SECRET;

    // AccessToken 생성
    public String createAccessToken(String email) {
        Date expiredDate = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        String accessToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();

        return accessToken;
    }

    // RefreshToken 생성
    public String createRefreshToken(String email) {
        Date expiredDate = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        String refreshToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();

        // redis에 저장
        redisTemplate.opsForValue().set(
                refreshToken,
                email,
                30,
                TimeUnit.DAYS);

        return refreshToken;
    }

    // jwt 검증
    public String validate(String jwt) throws ExpiredJwtException {
        String subject = null;
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();

        log.info(subject);
        return subject;
    }

    // refresh token 검증 & 유효 시 access token 재발급
    public List<String> reissue(String refreshToken) throws ExpiredJwtException, JwtException{
        String accessToken = null;
        String newRefreshToken = null;
        String email = null;
        List<String> tokens = new ArrayList<>();

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        try {
            email = redisTemplate.opsForValue().get(refreshToken);
            if (email == null) {
                log.info("refreshToken has been expried or doesn't have validation");
                return null;
            }

            Jwts.parser().setSigningKey(key).parseClaimsJws(refreshToken);
            accessToken = createAccessToken(email);
            newRefreshToken = createRefreshToken(email);

            redisTemplate.delete(refreshToken);

        } catch (ExpiredJwtException exception) {
            exception.printStackTrace();
            return null;
        } catch (JwtException exception) {
            exception.printStackTrace();
            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        tokens.add(accessToken);
        tokens.add(newRefreshToken);

        return tokens;
    }
}
