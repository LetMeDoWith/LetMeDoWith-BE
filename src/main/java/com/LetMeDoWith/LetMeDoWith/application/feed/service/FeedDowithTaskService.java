package com.LetMeDoWith.LetMeDoWith.application.feed.service;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveSuccessDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.SuccessDowithTaskQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedDowithTaskService {

    private final DowithTaskQueryRepository dowithTaskQueryRepository;

    /**
     * DowithTask 성공 이미지 피드 조회
     *
     * @param requestMemberId
     * @param pageable
     * @return
     */
    public RetrieveSuccessDowithTasksResult retrieveSuccessDowithTasks(String requestMemberId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        long totalCount = dowithTaskQueryRepository.countSuccessDowithTasks();
        List<SuccessDowithTaskQueryDto> successImages = dowithTaskQueryRepository.getSuccessDowithTasks(requestMemberId, offset, limit);

        Map<Long, Long> dowithTaskLikeCountMap = dowithTaskQueryRepository.countDowithTaskLikes(
                successImages.stream().map(SuccessDowithTaskQueryDto::id).collect(Collectors.toSet()));

        return RetrieveSuccessDowithTasksResult.of(totalCount, successImages, dowithTaskLikeCountMap);
    }

    /**
     * DowithTask 좋아요
     *
     * @param dowithTaskId
     * @param memberId
     */
    public void likeSuccessDowithTask(Long dowithTaskId, String memberId) {
        // Implementation for liking a DowithTask
    }
}
