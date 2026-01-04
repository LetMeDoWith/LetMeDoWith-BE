package com.LetMeDoWith.LetMeDoWith.batch.tasklet;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache.FeedCacheCommandRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.FeedQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class StoreFeedbackAvailableDowithTasksTasklet implements Tasklet {

    private final FeedQueryRepository feedQueryRepository;
    private final FeedCacheCommandRepository feedCacheCommandRepository;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        LocalDateTime targetDateTime = executionDateTime;

        if (executionDateTime == null) {
            targetDateTime = SystemTimeUtil.now();
        }

        // 잔소리 대상 두윗 조회 (QueryDSL)
        List<FeedbackAvailableDowithTaskQueryDto> dowithTasks =
                feedQueryRepository.getFeedbackAvailableDowithTasks(targetDateTime);

        // 잔소리 대상 두윗 상세 정보 Redis 적재
        feedCacheCommandRepository.refreshFeedbackAvailableDowithTasks(dowithTasks);

        return RepeatStatus.FINISHED;
    }
}
