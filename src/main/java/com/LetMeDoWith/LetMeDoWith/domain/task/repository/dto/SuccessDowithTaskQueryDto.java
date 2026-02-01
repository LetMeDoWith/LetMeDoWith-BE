package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

public record SuccessDowithTaskQueryDto(
        Long id, String title, String nickname, String profileImageUrl, String successImageUrl, Boolean isLiked) {
}
