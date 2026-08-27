package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

/** 두윗 Task에 좋아요를 남긴 회원 목록 조회 행 */
public record DowithTaskLikeMemberQueryDto(
        Long dowithTaskLikeId, String memberId, String nickname, String profileImageUrl) {}
