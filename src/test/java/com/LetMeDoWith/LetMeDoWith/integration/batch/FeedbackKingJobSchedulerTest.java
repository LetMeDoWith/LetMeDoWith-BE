package com.LetMeDoWith.LetMeDoWith.integration.batch;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FeedbackKingJobSchedulerTest extends AbstractIntegrationTest {

    private final List<String> expectedRankMemberIds = new ArrayList<>();

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

    private RankingTopic rankingTopic;

    @Override
    protected void deleteTestData() {
        rankingEntryJpaRepository.deleteAll();
        rankingEntryJpaRepository.flush();
        List<RankingTopic> rankingTopics = rankingTopicJpaRepository.findAll();
        rankingTopics.forEach(topic -> topic.updateCurrentRound(null));
        rankingTopicJpaRepository.saveAll(rankingTopics);
        rankingTopicJpaRepository.flush();
        rankingTopicJpaRepository.deleteAll();
        rankingTopicJpaRepository.flush();
        rankingTopicRoundJpaRepository.deleteAll();
        rankingTopicRoundJpaRepository.flush();
    }

    @Override
    protected void createTestData() {
        LocalDateTime aggregationStartDateTime = YearMonth.of(2026, 2).atDay(1).atTime(LocalTime.MIN);
        LocalDateTime aggregationEndDateTime =
                YearMonth.of(2026, 2).atEndOfMonth().atTime(LocalTime.MAX);
        this.rankingTopic = rankingTopicJpaRepository.save(
                RankingTopic.ofActive(RankingTopicCode.FEEDBACK_KING, "피드백 킹", "피드백 킹 랭킹 토픽입니다."));
        RankingTopicRound rankingTopicRound = rankingTopicRoundJpaRepository.save(
                RankingTopicRound.of(rankingTopic, 1L, aggregationStartDateTime, aggregationEndDateTime));
        this.rankingTopic.updateCurrentRound(rankingTopicRound);
        this.rankingTopic = rankingTopicJpaRepository.save(this.rankingTopic);

        // 1라운드 50명 세팅
        List<RankingEntry> rankingEntries = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            rankingEntries.add(RankingEntry.of(rankingTopicRound, "sender" + i, (long) i, null));
        }
        rankingEntryJpaRepository.saveAll(rankingEntries);

        // 잔소리 개수 Sender50에서 역순으로 순위 되도록 설정
        this.setFixedClock(LocalDateTime.of(2026, 3, 20, 15, 0));
        Long dowithTaskId = 1L;
        Long taskFeedbackTemplateId = 1L;
        for (int i = 1; i <= 50; i++) {
            if (i >= 25 && i <= 30) {
                // Sender25~30은 25개의 피드백을 보낸 것으로 세팅 - 동점자 처리
                for (int j = 1; j <= 25; j++) {
                    this.setFixedClock(LocalDateTime.of(2026, 3, 21, 15, 0).plusMinutes(i));
                    dowithTaskFeedbackJpaRepository.save(
                            DowithTaskFeedback.of("sender" + i, "receiver" + j, dowithTaskId, taskFeedbackTemplateId));
                }
            } else {
                for (int j = 1; j <= i; j++) {
                    dowithTaskFeedbackJpaRepository.save(
                            DowithTaskFeedback.of("sender" + i, "receiver" + j, dowithTaskId, taskFeedbackTemplateId));
                }
            }
        }

        /**
         * 테스트 데이터 설명
         */
        // 1 Round는 SenderN 오름차순으로 RankingEntry 삽입됨
        // 잔소리 더미 데이터는 아래와 같은 순서로 삽입됨
        // Sender50 -> 49 -> .... 31 -> 25 -> 26 -> ... 30 -> 24 .... -> Sender1 순으로 피드백 개수 역순 정렬되도록 세팅
        // Sender25 ~ 30은 동점자이고 오름차순으로 먼저 Feedback 보낸 사람이 높은 순위 되도록 세팅

        List<String> sameFeedbackCountRankMemberIds = new ArrayList<>();
        for (int i = 50; i >= 1; i--) {

            if (i == 24) {
                expectedRankMemberIds.addAll(sameFeedbackCountRankMemberIds);
            }

            if (i >= 25 && i <= 30) {
                // Sender25 ~ 30은 오름차순으로 랭크되도록 세팅
                sameFeedbackCountRankMemberIds.add("sender" + i);
                sameFeedbackCountRankMemberIds.sort(Comparator.naturalOrder());
            } else {
                expectedRankMemberIds.add("sender" + i);
            }
        }
    }

    @Test
    @DisplayName("라운드 1 집계 테스트 - 피드백 킹 랭킹 토픽의 1라운드 집계가 정상적으로 수행되어야 한다.")
    void round1AggregateTest() {

        // given
        // 매달 마지막 주 월요일 배치로 fixedTime 세팅
        this.setFixedClock(LocalDateTime.of(2026, 3, 30, 2, 0));
        rankingEntryJpaRepository.deleteAll();
        rankingEntryJpaRepository.flush();
        rankingTopic.updateCurrentRound(null);
        rankingTopicJpaRepository.save(rankingTopic);
        rankingTopicJpaRepository.flush();
        rankingTopicRoundJpaRepository.deleteAll();
        rankingTopicRoundJpaRepository.flush();

        // when
        feedbackKingJobScheduler.feedbackKingRankingJob();

        // then
        RankingTopicRound rankingTopicRound = rankingTopicRoundJpaRepository
                .findByRankingTopicAndRound(rankingTopic, 1L)
                .orElseThrow(() -> new RuntimeException("랭킹 토픽 라운드가 생성되지 않았습니다."));
        List<RankingEntry> rankingEntries =
                rankingEntryJpaRepository.findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(
                        rankingTopic.getId(), 1L);
        if (rankingEntries.isEmpty()) throw new RuntimeException("랭킹 엔트리가 생성되지 않았습니다.");
        rankingEntries.sort(Comparator.comparing(RankingEntry::getCurrentRank));
        for (int i = 0; i < expectedRankMemberIds.size(); i++) {
            RankingEntry rankingEntry = rankingEntries.get(i);
            assertThat(rankingEntry.getMemberId()).isEqualTo(expectedRankMemberIds.get(i));
            assertThat(rankingEntry.getCurrentRank()).isEqualTo((long) i + 1);
            assertThat(rankingEntry.getPreviousRank()).isNull();
        }
    }

    @Test
    @DisplayName("라운드 2 집계 테스트 - 피드백 킹 랭킹 토픽의 2라운드 집계 / 이전 순위 기록이 정상적으로 수행되어야 한다.")
    void round2AggregateTest() {

        // given
        // 매달 마지막 주 월요일 배치로 fixedTime 세팅
        this.setFixedClock(LocalDateTime.of(2026, 3, 30, 2, 0));

        // when
        feedbackKingJobScheduler.feedbackKingRankingJob();

        // then
        RankingTopicRound rankingTopicRound = rankingTopicRoundJpaRepository
                .findByRankingTopicAndRound(rankingTopic, 2L)
                .orElseThrow(() -> new RuntimeException("랭킹 토픽 라운드가 생성되지 않았습니다."));
        List<RankingEntry> rankingEntries =
                rankingEntryJpaRepository.findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(
                        rankingTopic.getId(), 2L);
        if (rankingEntries.isEmpty()) throw new RuntimeException("랭킹 엔트리가 생성되지 않았습니다.");
        rankingEntries.sort(Comparator.comparing(RankingEntry::getCurrentRank));
        for (int i = 0; i < expectedRankMemberIds.size(); i++) {
            RankingEntry rankingEntry = rankingEntries.get(i);
            assertThat(rankingEntry.getMemberId()).isEqualTo(expectedRankMemberIds.get(i));
            assertThat(rankingEntry.getCurrentRank()).isEqualTo((long) i + 1);
            int previousRank = Integer.parseInt(rankingEntry.getMemberId().replaceAll("\\D+", ""));
            assertThat(rankingEntry.getPreviousRank()).isEqualTo(previousRank);
        }
    }

    //    private void insertFirstRoundRankingEntryByBatch(int entryCount, Long rankingTopicRoundId) {
    //        String sql =
    //                """
    //                        INSERT INTO ranking_entry (
    //                            ranking_topic_round_id,
    //                            member_id,
    //                            current_rank,
    //                            previous_rank,
    //                            created_at,
    //                            created_by,
    //                            updated_at,
    //                            updated_by
    //                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    //                        """;
    //        LocalDateTime now = SystemTimeUtil.now();
    //        String auditingMemberId = "test-user";
    //
    //        List<Object[]> batchArgs = new ArrayList<>();
    //        int batchSize = 1000;
    //        for (int i = 1; i <= entryCount; i++) {
    //            batchArgs.add(new Object[] {
    //                rankingTopicRoundId, // ranking_topic_round_id
    //                "sender" + i, // member_id
    //                1L,
    //                null,
    //                now, // created_at
    //                auditingMemberId, // created_by
    //                now, // updated_at
    //                auditingMemberId // updated_by
    //            });
    //            if (batchArgs.size() >= batchSize) {
    //                jdbcTemplate.batchUpdate(sql, batchArgs);
    //                batchArgs.clear();
    //            }
    //        }
    //        if (!batchArgs.isEmpty()) {
    //            jdbcTemplate.batchUpdate(sql, batchArgs);
    //        }
    //    }
    //
    //    private void insertDowithTaskFeedbackByBatch(Long dowithTaskId, Long feedbackTemplateId) {
    //        String sql =
    //                """
    //                        INSERT INTO dowith_task_feedback (
    //                            dowith_task_id,
    //                            is_checked,
    //                            receiver_member_id,
    //                            sender_member_id,
    //                            task_feedback_template_id,
    //                            created_at,
    //                            created_by,
    //                            updated_at,
    //                            updated_by
    //                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    //                        """;
    //        LocalDateTime now = SystemTimeUtil.now();
    //        String auditingMemberId = "test-user";
    //        String isChecked = Yn.FALSE.getCode();
    //
    //        List<Object[]> batchArgs = new ArrayList<>();
    //        int batchSize = 1000;
    //        for (int i = 1; i <= 1000; i++) {
    //            for (int j = 1; j <= i; j++) {
    //                batchArgs.add(new Object[] {
    //                    dowithTaskId,
    //                    isChecked,
    //                    "receiver" + j,
    //                    "sender" + i,
    //                    feedbackTemplateId,
    //                    now,
    //                    auditingMemberId,
    //                    now,
    //                    auditingMemberId
    //                });
    //            }
    //            if (batchArgs.size() >= batchSize) {
    //                jdbcTemplate.batchUpdate(sql, batchArgs);
    //                batchArgs.clear();
    //            }
    //        }
    //        if (!batchArgs.isEmpty()) {
    //            jdbcTemplate.batchUpdate(sql, batchArgs);
    //        }
    //    }
}
