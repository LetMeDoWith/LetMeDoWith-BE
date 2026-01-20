package com.LetMeDoWith.LetMeDoWith.presentation.feed.dto;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveSuccessDowithTasksResult;
import java.util.List;

public record RetrieveSuccessDowithTasksRes(List<SuccessImage> successImages) {

    public static RetrieveSuccessDowithTasksRes from(RetrieveSuccessDowithTasksResult result) {
        List<SuccessImage> successImages = result.successDowithTasks().stream()
                .map(image -> new SuccessImage(
                        image.dowithTaskId(),
                        image.title(),
                        image.memberNickname(),
                        image.memberProfileImageUrl(),
                        image.successImageUrl(),
                        image.isLiked(),
                        image.likeCount()))
                .toList();
        return new RetrieveSuccessDowithTasksRes(successImages);
    }

    public record SuccessImage(
            Long dowithTaskId,
            String title,
            String memberNickname,
            String memberProfileImageUrl,
            String successImageUrl,
            Boolean isLiked,
            Long likeCount) {}
}
