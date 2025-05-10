package com.LetMeDoWith.LetMeDoWith.common.exception;

import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {

    private final FailResponseStatus status;

    public RestApiException(FailResponseStatus status) {
        this.status = status;
    }

    public RestApiException(FailResponseStatus status, Exception exception) {
        super(exception);
        this.status = status;
    }
}
