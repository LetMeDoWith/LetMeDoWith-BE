package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto;

public record RankingsQueryDto(
        Integer year,
        Integer month,
        Integer week,
        Long topicId,
        String topicTitle,
        Long currentRank,
        Long previousRank,
        String memberId,
        String nickname,
        String profileImageUrl) {}
