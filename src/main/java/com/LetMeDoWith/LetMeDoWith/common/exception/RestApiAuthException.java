package com.LetMeDoWith.LetMeDoWith.common.exception;

import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import lombok.Getter;

@Getter
public class RestApiAuthException extends RuntimeException {

    private FailResponseStatus status;

    public RestApiAuthException(FailResponseStatus status) {
        this.status = status;
    }

    public RestApiAuthException(FailResponseStatus status, Exception exception) {
        super(exception);
        this.status = status;
    }
}
