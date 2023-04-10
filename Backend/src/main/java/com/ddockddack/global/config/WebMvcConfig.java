package com.ddockddack.global.config;

import com.ddockddack.global.interceptor.CheckAdminInterceptor;
import com.ddockddack.global.interceptor.CheckBanUserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final CheckBanUserInterceptor checkBanUserInterceptor;
    private final CheckAdminInterceptor checkAdminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkBanUserInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns();
        registry.addInterceptor(checkAdminInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns();
    }
}
