package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "두윗모드 Task 잔여 생성 가능 개수 조회 응답")
public record GetRemainedDowithTaskCountRes(
        @Schema(description = "오늘(또는 정책 기준) 남은 두윗 Task 생성 가능 횟수", example = "3") int remainedDowithTaskCount) {}
