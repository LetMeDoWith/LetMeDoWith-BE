package com.LetMeDoWith.LetMeDoWith.application.feed.service;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache.FeedCacheQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.FeedQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedQueryRepository feedQueryRepository;
    private final FeedCacheQueryRepository feedCacheQueryRepository;

    /**
     * 잔소리 대상 두윗 목록 조회. Redis에 적재된 두윗 목록을 조회하며, 실패시 현재시간을 기준으로 잔소리 대상인 두윗을 가져옴
     *
     * @return 잔소리 대상 두윗
     */
    public RetrieveFeedbackAvailableDowithTasksResult retrieveFeedbackAvailableDowithTasks() {
        List<FeedbackAvailableDowithTaskQueryDto> feedbackAvailableDowithTasks =
            feedCacheQueryRepository.getFeedbackAvailableDowithTasks().stream()
                .filter(dto -> {
                    // dto 가 아직 잔소리 가능한 대상인지 (시작 시각 + 1시간이 현재보다 미래) 확인
                    return LocalDateTime.of(dto.date(), dto.startTime())
                        .plusHours(1)
                        .isAfter(SystemTimeUtil.now());
                })
                .toList();

        // fallback
        if (feedbackAvailableDowithTasks.isEmpty()) {
            return RetrieveFeedbackAvailableDowithTasksResult.from(
                feedQueryRepository.getFeedbackAvailableDowithTasks(SystemTimeUtil.now()));
        }

        return RetrieveFeedbackAvailableDowithTasksResult.from(feedbackAvailableDowithTasks);
    }
}