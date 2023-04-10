package com.ddockddack.global.interceptor;

import com.ddockddack.domain.admin.service.AdminService;
import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.entity.Role;
import com.ddockddack.domain.member.service.MemberService;
import com.ddockddack.domain.member.service.TokenService;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckAdminInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final AdminService adminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("CheckAdminInterceptor 진입");

        String accessToken = (request).getHeader("access-token");

        log.info("accessToken {} ", accessToken);


        if (!(accessToken == null || "".equals(accessToken))){

            Long adminId = tokenService.getUid(accessToken);

            // admin 확인
            if(adminService.isAdminByAccessToken(adminId)){
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }

        }

        throw new NotFoundException(ErrorCode.NOT_ADMIN);
    }
}
