package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveSuccessDowithTasksResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "인증 완료된 두윗 Task 목록 조회 응답")
public record RetrieveSuccessDowithTasksRes(
        @Schema(description = "성공 인증이 등록된 두윗 Task 목록") List<SuccessDowithTask> successDowithTasks) {

    public static RetrieveSuccessDowithTasksRes from(RetrieveSuccessDowithTasksResult result) {
        List<SuccessDowithTask> successDowithTasks = result.successDowithTasks().stream()
                .map(dowith -> new SuccessDowithTask(
                        dowith.id(),
                        dowith.title(),
                        dowith.nickname(),
                        dowith.profileImageUrl(),
                        dowith.successImageUrl(),
                        dowith.isLiked(),
                        dowith.likeCount()))
                .toList();
        return new RetrieveSuccessDowithTasksRes(successDowithTasks);
    }

    @Schema(description = "인증 완료 두윗 Task 한 건")
    public record SuccessDowithTask(
            @Schema(description = "Task ID", example = "1") Long id,
            @Schema(description = "제목", example = "아침 먹기") String title,
            @Schema(description = "닉네임", example = "스토디") String nickname,
            @Schema(description = "프로필사진", example = "https://storage/image.jpg") String profileImageUrl,
            @Schema(description = "성공인증사진", example = "https://storage/image.jpg") String successImageUrl,
            @Schema(description = "좋아요여부", example = "true") Boolean isLiked,
            @Schema(description = "좋아요수", example = "100") Long likeCount) {}
}
