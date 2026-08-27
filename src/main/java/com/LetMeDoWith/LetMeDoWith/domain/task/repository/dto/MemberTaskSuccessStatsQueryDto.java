package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

/**
 * 갓생실천러 랭킹 산출에 필요한 회원별 성공/전체 두윗 집계값
 */
public record MemberTaskSuccessStatsQueryDto(String memberId, Long registeredTaskCount, Long successTaskCount) {}
