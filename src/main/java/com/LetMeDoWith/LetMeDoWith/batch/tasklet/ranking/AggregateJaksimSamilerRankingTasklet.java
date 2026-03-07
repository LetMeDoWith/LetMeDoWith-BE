package com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking;

import com.LetMeDoWith.LetMeDoWith.batch.service.ranking.RankingBatchService;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.JaksimSamilerRankingBatchQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
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
    private final JaksimSamilerRankingBatchQueryRepository jaksimSamilerRankingBatchQueryRepository;

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
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime aggregationEndDateTime = executionDateTime
                .minusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(0);

        RankingTopicRound rankingTopicRound = rankingBatchService.createNextRound(
                rankingTopic, currentRound, aggregationStartDateTime, aggregationEndDateTime);

        List<RankingEntry> rankingEntries = RankingEntry.of(
                rankingTopicRound,
                jaksimSamilerRankingBatchQueryRepository.getRankingScores(
                        aggregationStartDateTime, aggregationEndDateTime),
                rankingBatchService.getPreviousRankMap(rankingTopic.getId(), previousRound));

        rankingBatchService.saveRankingEntries(rankingEntries);
        rankingBatchService.updateCurrentRound(rankingTopic, rankingTopicRound);

        log.info(
                "Finished jaksimSamilerRankingJob. topicId={}, round={}, entryCount={}",
                rankingTopic.getId(),
                rankingTopicRound.getRound(),
                rankingEntries.size());

        return RepeatStatus.FINISHED;
    }
}
