package com.ddockddack.domain.member.service;

import com.ddockddack.domain.member.oauth.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final Environment env;
//    private final RedisTemplate redisTemplate;
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder()
            .encodeToString(env.getProperty("jwt.token.secret-key").getBytes());
    }

    public Token generateToken(Long uid, String role) {
        long tokenPeriod = Long.parseLong(
            env.getProperty("jwt.access-token.expire-length")); // 15 min
        long refreshPeriod = Long.parseLong(
            env.getProperty("jwt.refresh-token.expire-length")); // 1 month

        Claims claims = Jwts.claims().setSubject(uid.toString());
        claims.put("role", role);

        Date now = new Date();
        return new Token(
            Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact(),
            Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact());
    }

    public Token reGenerateAccessToken(Long uid, String role, String refreshToken) {
        long tokenPeriod = Long.parseLong(
                env.getProperty("jwt.access-token.expire-length")); // 15 min

        Claims claims = Jwts.claims().setSubject(uid.toString());
        claims.put("role", role);

        Date now = new Date();
        return new Token(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + tokenPeriod))
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .compact(),
                refreshToken);
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            log.info("claims {}", Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token));
//            log.info("token verify {}", redisTemplate.opsForValue().get(token));
//            ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();

//            if (logoutValueOperations.get(token) != null) {
//                log.info("로그아웃 된 토큰입니다.");
//                return false;
//            }
            return claims.getBody()
                .getExpiration()
                .after(new Date());
        } catch (Exception e) {
            log.info("Err {}", "access 만료");
            return false;
        }
    }

    public Long getExpiration(String token) {
        try {
            log.info("expiration {}",
                Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody());
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
                .getExpiration();
            Long now = new Date().getTime();

            return expiration.getTime() - now;
        } catch (ExpiredJwtException e) {
            return -1L;
        }


    }

    public Long getUid(String token) {
        return Long.valueOf(
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    }
}