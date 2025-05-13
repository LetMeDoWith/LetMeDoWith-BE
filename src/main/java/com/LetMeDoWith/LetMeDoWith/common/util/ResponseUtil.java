package com.LetMeDoWith.LetMeDoWith.common.util;

import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.SuccessResponseStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtil {

    // SUCCESS
    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse() {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(SuccessResponseStatus.OK.getStatusCode())
                        .message(SuccessResponseStatus.OK.getMessage())
                        .data(null)
                        .build();
        return new ResponseEntity<>(dto, SuccessResponseStatus.OK.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(HttpHeaders httpHeaders) {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(SuccessResponseStatus.OK.getStatusCode())
                        .message(SuccessResponseStatus.OK.getMessage())
                        .data(null)
                        .build();
        return new ResponseEntity<>(dto, httpHeaders, SuccessResponseStatus.OK.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(T data) {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(SuccessResponseStatus.OK.getStatusCode())
                        .message(SuccessResponseStatus.OK.getMessage())
                        .data(data)
                        .build();
        return new ResponseEntity<>(dto, SuccessResponseStatus.OK.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponsePageDto<T>> createSuccessResponse(
            T data, Pageable pageable) {
        ResponsePageDto<T> dto =
                ResponsePageDto.<T>builder()
                        .statusCode(SuccessResponseStatus.OK.getStatusCode())
                        .message(SuccessResponseStatus.OK.getMessage())
                        .data(data)
                        .page(pageable.getOffset())
                        .size(pageable.getPageSize())
                        .build();
        return new ResponseEntity<>(dto, SuccessResponseStatus.OK.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(
            T data, HttpHeaders httpHeaders) {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(SuccessResponseStatus.OK.getStatusCode())
                        .message(SuccessResponseStatus.OK.getMessage())
                        .data(data)
                        .build();
        return new ResponseEntity<>(dto, httpHeaders, SuccessResponseStatus.OK.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponsePageDto<T>> createSuccessResponse(
            T data, HttpHeaders httpHeaders, Pageable pageable) {
        ResponsePageDto<T> dto =
                ResponsePageDto.<T>builder()
                        .statusCode(SuccessResponseStatus.OK.getStatusCode())
                        .message(SuccessResponseStatus.OK.getMessage())
                        .data(data)
                        .page(pageable.getOffset())
                        .size(pageable.getPageSize())
                        .build();
        return new ResponseEntity<>(dto, httpHeaders, SuccessResponseStatus.OK.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(
            SuccessResponseStatus status) {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(status.getStatusCode())
                        .message(status.getMessage())
                        .data(null)
                        .build();
        return new ResponseEntity<>(dto, status.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(
            SuccessResponseStatus status, HttpHeaders httpHeaders) {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(status.getStatusCode())
                        .message(status.getMessage())
                        .data(null)
                        .build();
        return new ResponseEntity<>(dto, httpHeaders, status.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(
            SuccessResponseStatus status, T data) {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(status.getStatusCode())
                        .message(status.getMessage())
                        .data(data)
                        .build();
        return new ResponseEntity<>(dto, status.getHttpStatusCode());
    }

    public static <T> ResponseEntity<ResponseDto<T>> createSuccessResponse(
            SuccessResponseStatus status, T data, HttpHeaders httpHeaders) {
        ResponseDto<T> dto =
                ResponseDto.<T>builder()
                        .statusCode(status.getStatusCode())
                        .message(status.getMessage())
                        .data(data)
                        .build();
        return new ResponseEntity<>(dto, httpHeaders, status.getHttpStatusCode());
    }
}
