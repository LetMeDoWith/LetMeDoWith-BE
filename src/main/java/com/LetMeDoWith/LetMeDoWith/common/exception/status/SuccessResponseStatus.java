package com.LetMeDoWith.LetMeDoWith.common.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessResponseStatus {
    OK("OK", "S100", HttpStatus.OK, "요청에 성공했습니다."),
    PROCEED_TO_SIGNUP("PROCEED_TO_SIGNUP", "S101", HttpStatus.OK, "가입되지 않은 사용자입니다. 회원가입을 진행하세요.");

    private final String statusName;
    private final String statusCode;
    private final HttpStatus httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
