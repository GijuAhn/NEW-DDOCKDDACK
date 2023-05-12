package com.ddockddack.global.filter;

import com.ddockddack.global.error.ErrorCode;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ErrorCode error = (ErrorCode)request.getAttribute("exception");
            setResponse(response, error);
        }
    }


    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        // 아래 분기를 통과하지 않는 예외는 모두 서버에러로 처리한다.
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if(errorCode.getCode() == 401) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        if(errorCode.getCode() == 400) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put("message", errorCode.getMessage());

        response.getWriter().print(responseJson);

    }
}
