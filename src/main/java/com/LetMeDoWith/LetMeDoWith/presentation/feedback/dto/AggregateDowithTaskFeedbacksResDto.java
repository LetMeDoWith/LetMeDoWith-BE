package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "잔소리 집계 최종 응답")
public record AggregateDowithTaskFeedbacksResDto(
        @Schema(description = "집계된 잔소리 템플릿 메시지", example = "집계된 잔소리 템플릿 메시지") List<FeedbackTemplateDto> aggregates) {}
