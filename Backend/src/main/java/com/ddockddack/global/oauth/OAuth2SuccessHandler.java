package com.ddockddack.global.oauth;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.member.service.TokenService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final RedisTemplate redisTemplate;
    @Value("${LOGIN_SUCCESS_URL}")
    private String loginSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        var attributes = oAuth2User.getAttributes();

        Member member = memberRepository.getByEmail((String) attributes.get("email"));

        if (member == null) {
            member = Member.builder()
                .email((String) attributes.get("email"))
                .nickname((String) attributes.get("nickname"))
                .profile("default_profile_img.png")
                .role(Role.MEMBER)
                .build();
            memberRepository.save(member);
        }

        String accessToken = tokenService.generateToken(member.getId(), "USER");

        Cookie cookie = new Cookie("refresh-token",
            tokenService.generateRefreshToken(member.getId(), "USER"));

        // expires in 7 days
        cookie.setMaxAge(60 * 60 * 24 * 7);

        // optional properties
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // add cookie to response
        response.addCookie(cookie);

        response.sendRedirect(loginSuccessUrl + accessToken);
    }
}