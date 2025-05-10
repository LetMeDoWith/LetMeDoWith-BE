package com.LetMeDoWith.LetMeDoWith.common.dto;

import lombok.Builder;

@Builder
public record ResponsePageDto<T>(
        String statusCode, String message, Long page, Integer size, T data) {}
