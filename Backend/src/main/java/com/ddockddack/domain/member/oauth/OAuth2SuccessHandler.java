package com.ddockddack.domain.member.oauth;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.member.response.MemberInfoRes;
import com.ddockddack.domain.member.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    @Value("${LOGIN_SUCCESS_URL}")
    private String loginSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        var attributes = oAuth2User.getAttributes();

        Member member = memberRepository.getByEmail((String) attributes.get("email"));

        System.out.println((String) attributes.get("email"));
        System.out.println(member);
        log.info("email {} ", (String) attributes.get("email"));
        log.info("email {} ", (String) attributes.get("nickname"));
        if (member == null) {
            member = Member.builder()
                .email((String) attributes.get("email"))
                .nickname((String) attributes.get("nickname"))
                .profile("default_profile_img.png")
                .role(Role.MEMBER)
                .build();
            memberRepository.save(member);
        }
        log.info("Member Id {} {}", member.getId(), member.getEmail());

        Token token = tokenService.generateToken(member.getId(), "USER");
        log.info("JwT : {}", token);

        Cookie cookie = new Cookie("refresh-token", token.getRefreshToken());
        // expires in 7 days
        cookie.setMaxAge(60 * 60 * 24* 7);

        // optional properties
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // add cookie to response
        response.addCookie(cookie);

        response.sendRedirect(loginSuccessUrl+token.getToken());
    }
}