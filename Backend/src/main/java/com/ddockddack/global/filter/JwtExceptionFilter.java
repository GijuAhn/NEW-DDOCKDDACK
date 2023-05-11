package com.ddockddack.global.filter;

import com.ddockddack.global.error.ErrorCode;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            //만료 에러
            System.out.println("여깅옴?");
            request.setAttribute("exception", ErrorCode.EXPIRED_ACCESSTOKEN);
//            filterChain.doFilter(request, response);
        }
    }
}
