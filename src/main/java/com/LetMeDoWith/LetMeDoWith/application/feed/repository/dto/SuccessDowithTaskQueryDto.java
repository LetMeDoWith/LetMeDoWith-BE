package com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto;

public record SuccessDowithTaskQueryDto(
        Long dowithTaskId,
        String title,
        String memberNickname,
        String memberProfileImageUrl,
        String successImageUrl,
        Boolean isLiked) {}
