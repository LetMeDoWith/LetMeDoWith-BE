package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto;

public record RankingsQueryDto(
        Long round,
        Long topicId,
        String topicTitle,
        Long currentRank,
        Long previousRank,
        String memberId,
        String nickname,
        String profileImageUrl) {}
