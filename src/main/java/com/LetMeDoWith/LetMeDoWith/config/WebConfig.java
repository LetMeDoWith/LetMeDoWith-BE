package com.LetMeDoWith.LetMeDoWith.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.LetMeDoWith.LetMeDoWith.interceptor.AuthenticateInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final AuthenticateInterceptor authenticateInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticateInterceptor)
			.addPathPatterns("/api/v1/**")
			.excludePathPatterns(
				"/health-check",
				"/api/v1/auth/token",
				"/api/v1/auth/token/refresh"
				);
			// TODO - 운영 반영 시 주석 해제
			// .excludePathPatterns("/**"); // 개발 시, 테스트를 위해 actvice
	}
}
