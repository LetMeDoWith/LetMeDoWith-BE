package com.LetMeDoWith.LetMeDoWith.common.util;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
@Slf4j
public class AuthUtil {

    private final String AUTHORIZATION_KEY = "AUTHORIZATION";

    public String getAccessToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authorizationHeader = request.getHeader(AUTHORIZATION_KEY);
        if (authorizationHeader == null) {
            throw new RestApiException(FailResponseStatus.ATK_NOT_EXIST);
        }
        if (!authorizationHeader.startsWith("Bearer")) {
            throw new RestApiException(FailResponseStatus.ATK_NOT_EXIST);
        }
        String accessToken = authorizationHeader.substring(6).trim();
        if (accessToken.isEmpty()) {
            throw new RestApiException(FailResponseStatus.ATK_NOT_EXIST);
        } else {
            return accessToken;
        }
    }

    /**
     * Request 에서 Signup Token을 가져온다.
     *
     * <p>TODO: Signup Token임을 구별할 수 있는 접두어가 필요할 것으로 보임.
     *
     * @return
     */
    public String getSignupToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authorizationHeader = request.getHeader(AUTHORIZATION_KEY);

        if (!authorizationHeader.startsWith("Bearer")) {
            throw new RestApiException(FailResponseStatus.INVALID_TOKEN);
        }

        String signupToken = authorizationHeader.substring(6).trim();
        if (signupToken.isEmpty()) {
            throw new RestApiException(FailResponseStatus.INVALID_TOKEN);
        }

        return signupToken;
    }

    public String getMemberId() {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getAttribute("memberId").toString();
    }
}
