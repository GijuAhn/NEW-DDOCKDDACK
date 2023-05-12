package com.ddockddack.domain.member.service;

import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.oauth.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final Environment env;
    private final RedisTemplate<String, RefreshToken> redisTemplate;
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder()
            .encodeToString(env.getProperty("jwt.token.secret-key").getBytes());
    }

    public String generateToken(Long uid, String role) {
        long tokenPeriod = Long.parseLong(
            env.getProperty("jwt.access-token.expire-length")); // 15 min

        Claims claims = Jwts.claims().setSubject(uid.toString());
        claims.put("role", role);

        Date now = new Date();

        final String accessToken = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + tokenPeriod))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

        return accessToken;
    }

    public String generateRefreshToken(Long uid, String role) {
        long refreshPeriod = Long.parseLong(
            env.getProperty("jwt.refresh-token.expire-length"));

        Claims claims = Jwts.claims().setSubject(uid.toString());
        claims.put("role", role);

        Date now = new Date();

        final String refresh = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + refreshPeriod))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
        RefreshToken refreshToken = new RefreshToken(uid, refresh);

        redisTemplate.opsForValue().set(refresh, refreshToken);
        redisTemplate.expire(refresh, 14, TimeUnit.DAYS);

        return refresh;

    }

    public void refreshTokenValidate(String refreshToken) {
        if (refreshToken == null) {
            throw new AccessDeniedException(ErrorCode.LOGIN_REQUIRED);
        }

        if (Optional.ofNullable(
            redisTemplate.opsForValue().get(refreshToken)).isEmpty()) {
            throw new AccessDeniedException(ErrorCode.LOGIN_REQUIRED);
        }

    }

    public boolean verifyToken(String token) {
        if (token == null) {
            throw new AccessDeniedException(ErrorCode.LOGIN_REQUIRED);
        }
        Jws<Claims> claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token);
        return claims.getBody()
            .getExpiration()
            .after(new Date());
    }


    public Long getExpiration(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
                .getExpiration();
            Long now = new Date().getTime();

            return expiration.getTime() - now;
        } catch (ExpiredJwtException e) {
            return -1L;
        }


    }

    public Long getUid(String token) {
        if (token == null) {
            throw new AccessDeniedException(ErrorCode.LOGIN_REQUIRED);
        }
        return Long.valueOf(
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    }
}