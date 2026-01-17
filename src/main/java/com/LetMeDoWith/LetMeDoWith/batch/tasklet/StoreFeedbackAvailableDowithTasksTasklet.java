package com.LetMeDoWith.LetMeDoWith.batch.tasklet;

import com.LetMeDoWith.LetMeDoWith.common.redis.StorePolicy;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feed.model.FeedDowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.feed.service.LazyDowithTaskSelector;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.FeedQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.redis.RedisOperator;
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

    public static final int LAZY_DOWITH_COUNT = 15;

    private final FeedQueryRepository feedQueryRepository;
    private final LazyDowithTaskSelector lazyDowithTaskSelector;
    private final RedisOperator redisOperator;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        LocalDateTime targetDateTime = executionDateTime;

        if (executionDateTime == null) {
            targetDateTime = SystemTimeUtil.now();
        }

        // 잔소리 대상 두윗 조회 (QueryDSL)
        List<FeedDowithTaskQueryDto> dowithTaskQueryDtos =
                feedQueryRepository.getFeedbackAvailableDowithTasks(targetDateTime);

        List<FeedDowithTask> feedDowithTasks = dowithTaskQueryDtos.stream()
                .map(dto -> {
                    return FeedDowithTask.of(
                            dto.id(),
                            dto.memberId(),
                            dto.nickname(),
                            dto.badgeImageUrl(),
                            dto.title(),
                            dto.status(),
                            dto.date(),
                            dto.startTime(),
                            dto.feedbackCount());
                })
                .toList();

        List<FeedDowithTask> lazyDowithTasks = lazyDowithTaskSelector.selectLazyDowithTasks(feedDowithTasks);
        List<String> lazyDowithTaskIds =
                lazyDowithTasks.stream().map(dowith -> dowith.id().toString()).toList();

        // 잔소리 대상 두윗 상세 정보 Redis 적재
        redisOperator.putHashes(StorePolicy.FEEDBACK_AVAILABLE_DOWITH_TASKS, feedDowithTasks, dto -> dto.id()
                .toString());

        // 잔소리 대상 두윗 ID 목록 Redis 적재
        if (lazyDowithTaskIds.isEmpty()) {
            redisOperator.delete(StorePolicy.LAZY_DOWITH_TASK_IDS, "");
        } else {
            // id는 atomic rename
            String tempKeySuffix = "temp_" + SystemTimeUtil.nowTime();
            redisOperator.pushRightAll(StorePolicy.LAZY_DOWITH_TASK_IDS, tempKeySuffix, lazyDowithTaskIds);
            redisOperator.rename(StorePolicy.LAZY_DOWITH_TASK_IDS, tempKeySuffix, "");
        }
        return RepeatStatus.FINISHED;
    }
}
