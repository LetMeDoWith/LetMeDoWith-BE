package com.LetMeDoWith.LetMeDoWith.application.feed.dto;

import java.util.List;

public record RetrieveDowithTaskSuccessImagesResult(
        Long totalCount,
        List<SuccessImage> successImages
) {
    public record SuccessImage(
            Long dowithTaskId,
            String title,
            String memberNickname,
            String memberProfileImageUrl,
            String successImageUrl,
            boolean isLiked,
            Long likeCount
    ) {
    }

}
