package com.LetMeDoWith.LetMeDoWith.application.feed.service;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveSuccessDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.repository.FeedDowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto.SuccessDowithTaskQueryDto;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedDowithTaskService {

    private final FeedDowithTaskQueryRepository feedDowithTaskQueryRepository;

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

        var totalCount = feedDowithTaskQueryRepository.countSuccessDowithTasks();
        var successImages = feedDowithTaskQueryRepository.getSuccessDowithTasks(requestMemberId, offset, limit);

        Map<Long, Long> dowithTaskLikeCountMap = feedDowithTaskQueryRepository.countDowithTaskLikes(
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
