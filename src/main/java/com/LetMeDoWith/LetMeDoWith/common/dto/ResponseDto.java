package com.LetMeDoWith.LetMeDoWith.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "요청에 대한 응답 스키마")
public record ResponseDto<T>(String statusCode, String message, T data) {}
