package com.LetMeDoWith.LetMeDoWith.integration.batch;

import com.LetMeDoWith.LetMeDoWith.batch.scheduler.FeedbackKingJobScheduler;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingEntryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicRoundJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FeedbackKingJobSchedulerTest extends AbstractIntegrationTest {

    @Autowired
    private RankingTopicJpaRepository rankingTopicJpaRepository;

    @Autowired
    private RankingTopicRoundJpaRepository rankingTopicRoundJpaRepository;

    @Autowired
    private RankingEntryJpaRepository rankingEntryJpaRepository;

    @Autowired
    private DowithTaskFeedbackJpaRepository dowithTaskFeedbackJpaRepository;

    @Autowired
    private FeedbackKingJobScheduler feedbackKingJobScheduler;

    @Override
    protected void deleteTestData() {}

    @Override
    protected void createTestData() {
        LocalDateTime aggregationStartDateTime = YearMonth.of(2026, 2).atDay(1).atTime(LocalTime.MIN);
        LocalDateTime aggregationEndDateTime =
                YearMonth.of(2026, 2).atEndOfMonth().atTime(LocalTime.MAX);
        RankingTopic rankingTopic = rankingTopicJpaRepository.save(
                RankingTopic.ofActive(RankingTopicCode.FEEDBACK_KING, "피드백 킹", "피드백 킹 랭킹 토픽입니다."));
        RankingTopicRound rankingTopicRound = rankingTopicRoundJpaRepository.save(
                RankingTopicRound.of(rankingTopic, 1L, aggregationStartDateTime, aggregationEndDateTime));

        List<RankingEntry> entries = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            entries.add(RankingEntry.of(rankingTopicRound, "sender" + i, (long) i, null));
        }
        rankingEntryJpaRepository.saveAll(entries);

        Long dowithTaskId = 1L;
        Long taskFeedbackTemplateId = 1L;
        List<DowithTaskFeedback> feedbacks = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            // id 역순으로 1등이 되도록 피드백 개수 세팅
            for (int j = 1; j <= i; j++) {
                feedbacks.add(
                        DowithTaskFeedback.of("sender" + i, "receiver" + j, dowithTaskId, taskFeedbackTemplateId));
            }
        }
        dowithTaskFeedbackJpaRepository.saveAll(feedbacks);
    }

    @Test
    @DisplayName("라운드 1 집계 테스트 - 피드백 킹 랭킹 토픽의 1라운드 집계가 정상적으로 수행되어야 한다.")
    void round1AggregateTest() {

        // given
        rankingTopicRoundJpaRepository.deleteAll();
        rankingTopicRoundJpaRepository.flush();
        rankingEntryJpaRepository.deleteAll();
        rankingEntryJpaRepository.flush();

        // when
        feedbackKingJobScheduler.feedbackKingRankingJob();

        // then

    }

    @Test
    @DisplayName("라운드 2 집계 테스트 - 피드백 킹 랭킹 토픽의 2라운드 집계 / 이전 순위 기록이 정상적으로 수행되어야 한다.")
    void round2AggregateTest() {}
}
