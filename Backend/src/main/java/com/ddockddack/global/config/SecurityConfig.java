package com.ddockddack.global.config;

import com.ddockddack.global.filter.JwtAuthFilter;
import com.ddockddack.domain.member.oauth.OAuth2SuccessHandler;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.member.service.CustomOAuth2UserService;
import com.ddockddack.domain.member.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()      // Http basic Auth  기반으로 로그인 인증창이 뜸.  disable 시에 인증창 뜨지 않음.
            .csrf().disable()       // rest api이므로 csrf 보안이 필요없으므로 disable처리.
            .sessionManagement().sessionCreationPolicy(
                SessionCreationPolicy.STATELESS)// jwt token으로 인증하므로 stateless 하도록 처리.
            .and()// 인증권한이 필요한 페이지.// 나머지 모든 요청 허용  ( 생략 가능 )
                .oauth2Login()
                .successHandler(successHandler)
                .userInfoEndpoint().userService(customOAuth2UserService);
//            .failureHandler(oAuth2AuthenticationFailureHandler);

        http.addFilterAfter(new JwtAuthFilter(tokenService, memberRepository),
            UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 필터 무시
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/static/**", "/js/**", "/webjars/**");
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/static/**", "/js/**", "/webjars/**");
//    }
}