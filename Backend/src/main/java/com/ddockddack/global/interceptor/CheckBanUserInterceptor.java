package com.ddockddack.global.interceptor;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.service.MemberService;
import com.ddockddack.domain.member.service.TokenService;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckBanUserInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("checkBanUser 진입");

        String accessToken = (request).getHeader("access-token");

        log.info("accessToken {} ", accessToken);


        if (accessToken == null || "".equals(accessToken)){

        }else {

            Long id = tokenService.getUid(accessToken);
            Member member = memberService.getMemberById(id).orElse(null);
            log.info("id, member {}, {}", id, member);
            // 정지가 풀리는 날이라면
            if (member.getRole() == Role.BAN && member.getReleaseDate().isBefore(LocalDate.now())){

                memberService.releaseMember(member.getId());

                // 정지가 풀리는 날이 아니라면
            } else if (member.getRole() == Role.BAN && !member.getReleaseDate().isBefore(LocalDate.now())){

                log.info("정지된 유저입니다. (" + member.getReleaseDate() + ")");
                throw new Exception("정지된 유저입니다. (" + member.getReleaseDate() + ")");

            }

        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
