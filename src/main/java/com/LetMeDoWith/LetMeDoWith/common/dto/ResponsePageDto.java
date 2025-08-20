package com.LetMeDoWith.LetMeDoWith.common.dto;

import lombok.Builder;

@Builder
public record ResponsePageDto<T>(
        String statusCode, String message, long page, int size, int totalPage, long totalCount, T data) {}
