package com.ddockddack.global.filter;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.member.service.TokenService;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AccessDeniedException;
import com.ddockddack.global.error.exception.NotFoundException;
import com.ddockddack.global.oauth.MemberDetail;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    @SneakyThrows
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws IOException, ServletException {

        String accessToken = (request).getHeader("access-token");
        String refreshToken = getRefreshToken(request.getCookies());

        // 액세스 토큰이 존재 하는 경우

        if (accessToken != null && !accessToken.isBlank()) {
            // 토큰이 만료된 경우
            if (!tokenService.verifyToken(accessToken)) {
                throw new AccessDeniedException(ErrorCode.EXPIRED_ACCESSTOKEN);

                // 리프레시 토큰이 만료 된 경우
//                if (!tokenService.verifyToken(refreshToken)) {
//                    throw new AccessDeniedException(ErrorCode.EXPIRED_ACCESSTOKEN);
//                }
//                // 리프레시 토큰이 존재하지 않거나 변조된 경우
//                if (!tokenService.isRefreshTokenValidate(refreshToken)) {
//                    throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
//                }
//                // 리프레시 토큰이 유효한 경우 액세스 토큰 재발급
//                accessToken = tokenService.generateToken(
//                    tokenService.getUid(refreshToken), "USER");
            }
            // 유효한 토큰의 경우 정상 진행
//            setAuthentication(accessToken);
        }
        //  로그인이 필요한 요청의 경우
        if (isLoginRequest(request)) {
            // 액세스 토큰 없고, 리프레시 토큰이 만료 된 경우
            if (!tokenService.verifyToken(refreshToken)) {
                throw new AccessDeniedException(ErrorCode.EXPIRED_ACCESSTOKEN);
            }
            // 리프레시 토큰이 위조되었거나 , 없는 경우
            if (!tokenService.isRefreshTokenValidate(refreshToken)) {
                throw new AccessDeniedException(ErrorCode.NOT_AUTHORIZED);
            }
            // 리프레시 토큰이 존재하고, 검증을 통과한 경우에는 정상진행
            if (refreshToken != null && tokenService.verifyToken(refreshToken)) {
                accessToken = tokenService.generateToken(
                    tokenService.getUid(refreshToken), "USER");
                setAuthentication(accessToken);
            }
        }
        filterChain.doFilter(request, response);

    }


    private void setAuthentication(String accessToken) {
        Long id = tokenService.getUid(accessToken);
        Member member = memberRepository.findById(id).orElseThrow(() ->
            new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        MemberDetail memberAccessRes = new MemberDetail(accessToken, member.getId(),
            member.getRole());

        Authentication auth = getAuthentication(memberAccessRes);
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    private Authentication getAuthentication(MemberDetail member) {
        return new UsernamePasswordAuthenticationToken(member, "",
            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private String getRefreshToken(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh-token")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        if (request.getMethod().equals("GET")) {
            return false;
        }
        if (request.getRequestURI().contains("game-rooms") || (request.getRequestURI()
            .contains("single-games"))) {
            return false;
        }
        return true;
    }
}