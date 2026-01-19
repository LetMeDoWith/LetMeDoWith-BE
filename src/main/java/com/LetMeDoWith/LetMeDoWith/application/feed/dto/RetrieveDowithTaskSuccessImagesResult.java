package com.LetMeDoWith.LetMeDoWith.application.feed.dto;

import com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto.DowithTaskSuccessImageQueryDto;
import java.util.List;
import java.util.Map;

public record RetrieveDowithTaskSuccessImagesResult(Long totalCount, List<SuccessImage> successImages) {
    public static RetrieveDowithTaskSuccessImagesResult of(
            Long totalCount,
            List<DowithTaskSuccessImageQueryDto> successImageQueryDtos,
            Map<Long, Long> dowithTaskLikeCountMap) {
        return new RetrieveDowithTaskSuccessImagesResult(
                totalCount,
                successImageQueryDtos.stream()
                        .map(dto -> new SuccessImage(
                                dto.dowithTaskId(),
                                dto.title(),
                                dto.memberNickname(),
                                dto.memberProfileImageUrl(),
                                dto.successImageUrl(),
                                dto.isLiked() != null && dto.isLiked(),
                                dowithTaskLikeCountMap.getOrDefault(dto.dowithTaskId(), 0L)))
                        .toList());
    }

    public record SuccessImage(
            Long dowithTaskId,
            String title,
            String memberNickname,
            String memberProfileImageUrl,
            String successImageUrl,
            boolean isLiked,
            Long likeCount) {}
}
