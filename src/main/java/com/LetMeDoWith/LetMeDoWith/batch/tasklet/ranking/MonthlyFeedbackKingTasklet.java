package com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking;

import com.LetMeDoWith.LetMeDoWith.batch.service.RankingBatchService;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.SortDirection;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.CountSentFeedback;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
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
@RequiredArgsConstructor
@Slf4j
public class MonthlyFeedbackKingTasklet implements Tasklet {

    private final RankingBatchService rankingBatchService;

    //    private final RankingRepository rankingRepository;
    private final DowithTaskFeedbackQueryRepository dowithTaskFeedbackQueryRepository;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        LocalDateTime aggregationStartDateTime = executionDateTime.with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime aggregationEndDateTime =
                executionDateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // RankingTopic 조회
        //        RankingTopic rankingTopic =
        // rankingBatchService.getRankingTopic(RankingTopicCode.FEEDBACK_KING).orElseThrow(() -> new RuntimeException(
        //                "RankingTopicCode " + RankingTopicCode.FEEDBACK_KING + "에 해당하는 RankingTopic이 존재하지 않습니다."));

        // RankingTopic에 맞는 Ranking 기준으로 Feedback 순위 조회
        List<CountSentFeedback> countSentFeedbacks = dowithTaskFeedbackQueryRepository.getCountSentFeedbacks(
                aggregationStartDateTime.toLocalDate(), aggregationEndDateTime.toLocalDate(), SortDirection.DESC);

        // RankingTopicRound insert
        //        Optional<RankingTopicRound> opLatestRankingTopicRound =
        //                rankingRepository.getLatestRankingTopicRound(rankingTopic);
        //        Long round = 0L;
        //        if (opLatestRankingTopicRound.isPresent()) {
        //            round = opLatestRankingTopicRound.get().getRound() + 1;
        //        }
        //        RankingTopicRound rankingTopicRound = rankingRepository.save(
        //                RankingTopicRound.of(rankingTopic, round, aggregationStartDateTime, aggregationEndDateTime));

        Map<String, Long> memberIdRankMap = new HashMap<>();
        for (int i = 0; i < countSentFeedbacks.size(); i++) {
            CountSentFeedback countSentFeedback = countSentFeedbacks.get(i);
            memberIdRankMap.put(countSentFeedback.memberId(), (long) i + 1);
        }

        rankingBatchService.createRanking(
                RankingTopicCode.FEEDBACK_KING, aggregationStartDateTime, aggregationEndDateTime, memberIdRankMap);

        return RepeatStatus.FINISHED;
    }
}
