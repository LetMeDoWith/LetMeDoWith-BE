package com.LetMeDoWith.LetMeDoWith.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.LetMeDoWith.LetMeDoWith.provider.AuthTokenProvider;
import com.LetMeDoWith.LetMeDoWith.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateInterceptor implements HandlerInterceptor {

	private final AuthTokenProvider authTokenProvider;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {

		if(request.getMethod().equals("OPTIONS")) {
			return true;
		}

		Long memberId = authTokenProvider.validateToken(AuthUtil.getAccessToken(), AuthTokenProvider.TokenType.ATK);
		request.setAttribute("memberId", memberId);

		return true;

	}
}
