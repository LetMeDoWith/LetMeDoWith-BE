package com.LetMeDoWith.LetMeDoWith.application.feed.dto;

import com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto.SuccessDowithTaskQueryDto;
import java.util.List;
import java.util.Map;

public record RetrieveSuccessDowithTasksResult(Long totalCount, List<SuccessDowithTask> successDowithTasks) {
    public static RetrieveSuccessDowithTasksResult of(
            Long totalCount,
            List<SuccessDowithTaskQueryDto> successImageQueryDtos,
            Map<Long, Long> dowithTaskLikeCountMap) {
        return new RetrieveSuccessDowithTasksResult(
                totalCount,
                successImageQueryDtos.stream()
                        .map(dto -> new SuccessDowithTask(
                                dto.dowithTaskId(),
                                dto.title(),
                                dto.memberNickname(),
                                dto.memberProfileImageUrl(),
                                dto.successImageUrl(),
                                dto.isLiked() != null && dto.isLiked(),
                                dowithTaskLikeCountMap.getOrDefault(dto.dowithTaskId(), 0L)))
                        .toList());
    }

    public record SuccessDowithTask(
            Long dowithTaskId,
            String title,
            String memberNickname,
            String memberProfileImageUrl,
            String successImageUrl,
            boolean isLiked,
            Long likeCount) {}
}
