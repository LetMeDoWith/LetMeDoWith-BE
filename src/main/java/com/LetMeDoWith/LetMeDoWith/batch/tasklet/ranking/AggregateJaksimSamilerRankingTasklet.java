package com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking;

import com.LetMeDoWith.LetMeDoWith.batch.service.ranking.RankingBatchService;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
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
public class AggregateJaksimSamilerRankingTasklet implements Tasklet {

    private static final String JAKSIM_SAMILER_TOPIC_TITLE = "작심삼일러";

    private final RankingBatchService rankingBatchService;
    private final DowithTaskQueryRepository dowithTaskQueryRepository;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        RankingTopic rankingTopic = rankingBatchService
                .getRankingTopic(JAKSIM_SAMILER_TOPIC_TITLE)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        RankingTopicRound currentRound = rankingTopic.getCurrentRound();
        Long previousRound = currentRound == null ? null : currentRound.getRound();
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

        RankingTopicRound rankingTopicRound = rankingBatchService.createNextRound(
                rankingTopic, currentRound, aggregationStartDateTime, aggregationEndDateTime);

        List<FailedDowithTaskCountQueryDto> failedTaskCountsByMember =
                dowithTaskQueryRepository.getFailedTaskCountsByMember(aggregationStartDateTime, aggregationEndDateTime);
        List<RankingEntry> rankingEntries = rankingBatchService.createRankingEntries(
                rankingTopic.getId(), previousRound, rankingTopicRound, createCurrentRankMap(failedTaskCountsByMember));

        rankingBatchService.saveRankingEntries(rankingEntries);
        rankingBatchService.updateCurrentRound(rankingTopic, rankingTopicRound);

        log.info(
                "Finished jaksimSamilerRankingJob. topicId={}, round={}, entryCount={}",
                rankingTopic.getId(),
                rankingTopicRound.getRound(),
                rankingEntries.size());

        return RepeatStatus.FINISHED;
    }

    private Map<String, Long> createCurrentRankMap(List<FailedDowithTaskCountQueryDto> failedTaskCountsByMember) {
        Map<String, Long> currentRankMap = new LinkedHashMap<>();

        for (int index = 0; index < failedTaskCountsByMember.size(); index++) {
            FailedDowithTaskCountQueryDto failedTaskCount = failedTaskCountsByMember.get(index);
            currentRankMap.put(failedTaskCount.memberId(), (long) index + 1);
        }

        return currentRankMap;
    }
}
