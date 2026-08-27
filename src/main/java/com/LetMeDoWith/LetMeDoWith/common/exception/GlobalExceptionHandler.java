package com.LetMeDoWith.LetMeDoWith.common.exception;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_PARAM_ERROR;

import com.LetMeDoWith.LetMeDoWith.common.dto.FailResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.dto.InvalidParamResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({RestApiException.class})
    protected ResponseEntity<FailResponseDto> handleRestApiException(RestApiException ex) {
        log.error("{}: {}", ex.getStatus().getStatusName(), ex.getMessage(), ex);

        FailResponseDto responseBody = FailResponseDto.builder()
                .statusCode(ex.getStatus().getStatusCode())
                .message(ex.getMessage() == null ? ex.getStatus().getMessage() : ex.getMessage())
                .build();
        return new ResponseEntity<>(responseBody, ex.getStatus().getHttpStatusCode());
    }

    @ExceptionHandler({RestApiAuthException.class})
    protected ResponseEntity<FailResponseDto> handleRestApiAuthException(RestApiAuthException ex) {
        log.error("{}: {}", ex.getStatus(), ex.getMessage(), ex);

        FailResponseDto responseBody = FailResponseDto.builder()
                .statusCode(ex.getStatus().getStatusCode()) // TODO -
                // Auth 관련
                // Error
                // Code Enum
                // 따로 관리하고,
                // 해당 부분은 항상 같은값을
                // Response / 내부에서만 ErrorCode 로깅 외부에는 인증 오류 원인 숨기기
                .message(ex.getStatus().getMessage())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<InvalidParamResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.error("{}: {}", ex.getCause().getMessage(), ex.getMessage(), ex);

        Map<String, String> invalidParamMap = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            invalidParamMap.put(fieldName, errorMessage);
        });
        InvalidParamResponseDto responseBody = InvalidParamResponseDto.builder()
                .statusCode(INVALID_PARAM_ERROR.getStatusCode())
                .message(INVALID_PARAM_ERROR.getMessage())
                .invalidParams(invalidParamMap)
                .build();
        return new ResponseEntity<>(responseBody, INVALID_PARAM_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<FailResponseDto> handleException(Exception ex) {
        log.error("{}: {}", ex.getCause().getMessage(), ex.getMessage(), ex);

        FailResponseDto responseBody = FailResponseDto.builder()
                .statusCode(FailResponseStatus.INTERNAL_SERVER_ERROR.getStatusCode())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(responseBody, FailResponseStatus.INTERNAL_SERVER_ERROR.getHttpStatusCode());
    }
}
