package com.LetMeDoWith.LetMeDoWith.batch.tasklet;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicy;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.FeedQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
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
public class StoreFeedbackAvailableDowithAndLazyDowithMemberTasklet implements Tasklet {

    private final FeedQueryRepository feedQueryRepository;
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
        List<FeedbackAvailableDowithTaskQueryDto> dowithTaskList =
                feedQueryRepository.getFeedbackAvailableDowithTasks(targetDateTime);

        // 두윗 id 인덱스 계산
        List<Long> dowithIdList = dowithTaskList.stream()
                .map(FeedbackAvailableDowithTaskQueryDto::id)
                .toList();

        // 잔소리 대상 두윗 및 id 인덱스 Redis 적재
        redisOperator.pushRightAll(CachePolicy.DOWITH_TASK_IDS, "", dowithIdList);
        redisOperator.putHashes(CachePolicy.DOWITH_TASK, dowithTaskList, dto -> String.valueOf(dto.id()));

        return RepeatStatus.FINISHED;
    }
}
