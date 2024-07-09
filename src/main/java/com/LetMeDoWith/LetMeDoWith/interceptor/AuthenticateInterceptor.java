package com.LetMeDoWith.LetMeDoWith.interceptor;

import com.LetMeDoWith.LetMeDoWith.provider.AuthTokenProvider;
import com.LetMeDoWith.LetMeDoWith.provider.AuthTokenProvider.TokenType;
import com.LetMeDoWith.LetMeDoWith.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateInterceptor implements HandlerInterceptor {
    
    private final AuthTokenProvider authTokenProvider;
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
        throws Exception {
        String SIGNUP_COMPLETE_API_URI = "/api/v1/member";
        String SIGNUP_COMPLETE_API_METHOD = "PUT";
        
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // Do not block preflight
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        
        // 회원가입 완료 API 요청은 SIGNUP token으로 인증한다.
        boolean isSignupCompleteReq =
            uri.equals(SIGNUP_COMPLETE_API_URI) && method.equals(SIGNUP_COMPLETE_API_METHOD);
        
        String tokenToBeValidated =
            isSignupCompleteReq ? AuthUtil.getSignupToken() : AuthUtil.getAccessToken();
        TokenType tokenType = isSignupCompleteReq ? TokenType.SIGNUP : TokenType.ATK;
        
        Long memberId = authTokenProvider.validateToken(tokenToBeValidated, tokenType);
        request.setAttribute("memberId", memberId);
        
        return true;
        
    }
}