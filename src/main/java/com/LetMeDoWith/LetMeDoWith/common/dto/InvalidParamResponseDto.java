package com.LetMeDoWith.LetMeDoWith.common.dto;

import java.util.Map;
import lombok.Builder;

@Builder
public record InvalidParamResponseDto(String statusCode, String message, Map<String, String> invalidParams) {}
