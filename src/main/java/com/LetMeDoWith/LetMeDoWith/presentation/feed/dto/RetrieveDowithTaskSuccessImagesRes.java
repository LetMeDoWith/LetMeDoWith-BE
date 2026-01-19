package com.LetMeDoWith.LetMeDoWith.presentation.feed.dto;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveDowithTaskSuccessImagesResult;
import java.util.List;

public record RetrieveDowithTaskSuccessImagesRes(List<SuccessImage> successImages) {

    public static RetrieveDowithTaskSuccessImagesRes from(RetrieveDowithTaskSuccessImagesResult result) {
        List<SuccessImage> successImages = result.successImages().stream()
                .map(image -> new SuccessImage(
                        image.dowithTaskId(),
                        image.title(),
                        image.memberNickname(),
                        image.memberProfileImageUrl(),
                        image.successImageUrl(),
                        image.isLiked(),
                        image.likeCount()))
                .toList();
        return new RetrieveDowithTaskSuccessImagesRes(successImages);
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
