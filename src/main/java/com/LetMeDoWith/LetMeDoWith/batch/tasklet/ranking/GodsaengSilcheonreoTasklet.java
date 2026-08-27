package com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking;

import com.LetMeDoWith.LetMeDoWith.batch.dto.CreateRankingResult;
import com.LetMeDoWith.LetMeDoWith.batch.service.RankingBatchService;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.MemberTaskSuccessStatsQueryDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
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
public class GodsaengSilcheonreoTasklet implements Tasklet {

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

        List<MemberTaskSuccessStatsQueryDto> memberTaskSuccessStats =
                dowithTaskQueryRepository.getTaskSuccessStatsByMember(aggregationStartDateTime, aggregationEndDateTime);

        // 두윗 성공률은 코드에서 계산
        List<MemberTaskSuccessStatsQueryDto> sortedStats = memberTaskSuccessStats.stream()
                .sorted(Comparator.comparing(this::calculateSuccessRate)
                        .reversed()
                        .thenComparing(MemberTaskSuccessStatsQueryDto::successTaskCount, Comparator.reverseOrder())
                        .thenComparing(MemberTaskSuccessStatsQueryDto::registeredTaskCount, Comparator.reverseOrder())
                        .thenComparing(MemberTaskSuccessStatsQueryDto::memberId))
                .toList();

        Map<String, Long> currentRankMap = new LinkedHashMap<>();
        for (int index = 0; index < sortedStats.size(); index++) {
            currentRankMap.put(sortedStats.get(index).memberId(), (long) index + 1);
        }

        CreateRankingResult result = rankingBatchService.createRanking(
                RankingTopicCode.GODSAENG_SILCHEONREO,
                aggregationStartDateTime,
                aggregationEndDateTime,
                currentRankMap);

        log.info(
                "Finished godsaengSilcheonreoRankingJob. topicId={}, round={}, entryCount={}",
                result.rankingTopic().getId(),
                result.rankingTopicRound().getRound(),
                result.rankingEntrySize());

        return RepeatStatus.FINISHED;
    }

    // 분모가 0인 비정상 케이스는 방어적으로 0으로 처리한다.
    private BigDecimal calculateSuccessRate(MemberTaskSuccessStatsQueryDto stats) {
        if (stats.registeredTaskCount() == null || stats.registeredTaskCount() == 0L) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(stats.successTaskCount())
                .divide(BigDecimal.valueOf(stats.registeredTaskCount()), 10, RoundingMode.HALF_UP);
    }
}
