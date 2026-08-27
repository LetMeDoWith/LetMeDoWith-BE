package com.LetMeDoWith.LetMeDoWith.common.dto;

import lombok.Builder;

@Builder
public record FailResponseDto(String statusCode, String message) {}
