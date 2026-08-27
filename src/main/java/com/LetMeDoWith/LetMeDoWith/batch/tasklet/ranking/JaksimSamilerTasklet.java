package com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking;

import com.LetMeDoWith.LetMeDoWith.batch.dto.CreateRankingResult;
import com.LetMeDoWith.LetMeDoWith.batch.service.RankingBatchService;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.FailedDowithTaskCountQueryDto;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
@Slf4j
@RequiredArgsConstructor
public class JaksimSamilerTasklet implements Tasklet {

    private final RankingBatchService rankingBatchService;
    private final DowithTaskQueryRepository dowithTaskQueryRepository;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        LocalDateTime aggregationStartDateTime = executionDateTime
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime aggregationEndDateTime = executionDateTime
                .with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY))
                .minusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(0);

        List<FailedDowithTaskCountQueryDto> failedTaskCountsByMember =
                dowithTaskQueryRepository.getFailedTaskCountsByMember(aggregationStartDateTime, aggregationEndDateTime);

        Map<String, Long> currentRankMap = new LinkedHashMap<>();
        for (int i = 0; i < failedTaskCountsByMember.size(); i++) {
            FailedDowithTaskCountQueryDto failedTaskCount = failedTaskCountsByMember.get(i);
            currentRankMap.put(failedTaskCount.memberId(), (long) i + 1);
        }

        CreateRankingResult result = rankingBatchService.createRanking(
                RankingTopicCode.JAKSIM_SAMILER, aggregationStartDateTime, aggregationEndDateTime, currentRankMap);

        log.info(
                "Finished jaksimSamilerRankingJob. topicId={}, round={}, entryCount={}",
                result.rankingTopic().getId(),
                result.rankingTopicRound().getRound(),
                result.rankingEntrySize());

        return RepeatStatus.FINISHED;
    }
}
