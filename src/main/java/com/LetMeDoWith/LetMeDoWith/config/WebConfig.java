package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.common.converter.member.FollowTypeConverter;
import com.LetMeDoWith.LetMeDoWith.common.converter.member.GenderConverter;
import com.LetMeDoWith.LetMeDoWith.common.interceptor.AuthenticateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticateInterceptor authenticateInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(authenticateInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/health-check",
                        "/api/v1/auth/token",
                        "/api/v1/auth/token/refresh",
                        "/api/v1/auth/token/temp",
                        "/api/v1/members/nickname");
        // TODO - 운영 반영 시 주석 해제
        // .excludePathPatterns("/**"); // 개발 시, 테스트를 위해 actvice
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new FollowTypeConverter());
        registry.addConverter(new GenderConverter());
    }
}
