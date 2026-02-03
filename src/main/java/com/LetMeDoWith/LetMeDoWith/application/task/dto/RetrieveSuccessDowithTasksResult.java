package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.SuccessDowithTaskQueryDto;
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
                                dto.id(),
                                dto.title(),
                                dto.nickname(),
                                dto.profileImageUrl(),
                                dto.successImageUrl(),
                                dto.isLiked() != null && dto.isLiked(),
                                dowithTaskLikeCountMap.getOrDefault(dto.id(), 0L)))
                        .toList());
    }

    public record SuccessDowithTask(
            Long id,
            String title,
            String nickname,
            String profileImageUrl,
            String successImageUrl,
            boolean isLiked,
            Long likeCount) {}
}
