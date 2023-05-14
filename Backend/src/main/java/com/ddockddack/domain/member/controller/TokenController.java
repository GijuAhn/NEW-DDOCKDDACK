package com.ddockddack.domain.member.controller;

import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.member.service.TokenService;
import com.ddockddack.global.oauth.MemberDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/token")
public class TokenController {

    private final MemberRepository memberRepository;

    private final TokenService tokenService;
//    private final RedisTemplate redisTemplate;

    //  Access token expire endpoint
    @GetMapping("/expired")
    public String auth() {
        throw new RuntimeException();
    }

    @Operation(summary = "Access 토큰 재발급", description = "Access 토큰 재발급 메소드입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "사용자 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/refresh")
    public ResponseEntity refreshAuth(HttpServletResponse response) {
        MemberDetail memberDetail = (MemberDetail) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        System.out.println("여기 몇번떳냐");
        System.out.println(memberDetail.getAccessToken());
        response.addHeader("access-token", memberDetail.getAccessToken());
        return ResponseEntity.ok().build();
    }
}
