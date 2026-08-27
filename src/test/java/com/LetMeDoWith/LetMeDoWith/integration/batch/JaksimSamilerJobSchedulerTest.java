package com.LetMeDoWith.LetMeDoWith.integration.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.batch.scheduler.JaksimSamilerRankingJobScheduler;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingEntryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicRoundJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

class JaksimSamilerJobSchedulerTest extends AbstractIntegrationTest {

    private static final String TOPIC_TITLE = "작심삼일러";
    private final List<Member> extraMembers = new ArrayList<>();

    @Autowired
    private JaksimSamilerRankingJobScheduler jaksimSamilerRankingJobScheduler;

    @Autowired
    private RankingTopicJpaRepository rankingTopicJpaRepository;

    @Autowired
    private RankingTopicRoundJpaRepository rankingTopicRoundJpaRepository;

    @Autowired
    private RankingEntryJpaRepository rankingEntryJpaRepository;

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private RankingTopic rankingTopic;

    @Override
    protected void deleteTestData() {
        List<RankingTopic> topics = rankingTopicJpaRepository.findAll();
        topics.forEach(topic -> topic.updateCurrentRound(null));
        if (!topics.isEmpty()) {
            rankingTopicJpaRepository.saveAll(topics);
        }
        rankingEntryJpaRepository.deleteAll();
        rankingTopicRoundJpaRepository.deleteAll();
        rankingTopicJpaRepository.deleteAll();
        dowithTaskJpaRepository.deleteAll();
        if (!extraMembers.isEmpty()) {
            memberJpaRepository.deleteAll(extraMembers);
            extraMembers.clear();
        }
    }

    @Override
    protected void createTestData() {
        rankingTopic = rankingTopicJpaRepository.save(
                RankingTopic.ofActive(RankingTopicCode.JAKSIM_SAMILER, TOPIC_TITLE, "test"));
    }

    @Test
    @DisplayName("[SUCCESS] 마지막 주 월요일 02시 실행 시 1회차 엔트리 생성")
    void runJaksimSamilerRankingJob_firstRound_success() {
        // given
        Member memberB = createExtraMember("jaksim-batch-b");
        Member memberC = createExtraMember("jaksim-batch-c");

        createFailTask(
                requestMember.getId(),
                LocalDate.of(2026, 3, 10),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 10, 8, 0));
        createFailTask(
                requestMember.getId(),
                LocalDate.of(2026, 3, 12),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 12, 8, 0));
        createFailTask(
                memberB.getId(), LocalDate.of(2026, 3, 5), LocalTime.of(9, 0), LocalDateTime.of(2026, 3, 5, 7, 0));
        createFailTask(
                memberC.getId(), LocalDate.of(2026, 3, 5), LocalTime.of(9, 0), LocalDateTime.of(2026, 3, 5, 8, 0));

        // when
        setFixedClock(LocalDateTime.of(2026, 3, 30, 2, 0));
        jaksimSamilerRankingJobScheduler.runJaksimSamilerRankingJob();

        // then
        Long currentRoundId = ((Number) entityManager
                        .createNativeQuery("SELECT current_round_id FROM ranking_topic WHERE id = :topicId")
                        .setParameter("topicId", rankingTopic.getId())
                        .getSingleResult())
                .longValue();
        RankingTopicRound currentRound =
                rankingTopicRoundJpaRepository.findById(currentRoundId).orElseThrow();
        assertThat(currentRound.getRound()).isEqualTo(1L);
        assertThat(currentRound.getAggregationStartDateTime()).isEqualTo(LocalDateTime.of(2026, 3, 1, 0, 0));
        assertThat(currentRound.getAggregationEndDateTime()).isEqualTo(LocalDateTime.of(2026, 3, 29, 23, 59, 59));

        List<RankingEntry> entries =
                rankingEntryJpaRepository
                        .findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(rankingTopic.getId(), 1L)
                        .stream()
                        .sorted(Comparator.comparing(RankingEntry::getCurrentRank))
                        .toList();

        assertThat(entries).hasSize(3);
        assertThat(entries.get(0).getMemberId()).isEqualTo(requestMember.getId());
        assertThat(entries.get(0).getPreviousRank()).isNull();
        assertThat(entries.get(1).getMemberId()).isEqualTo(memberB.getId());
        assertThat(entries.get(2).getMemberId()).isEqualTo(memberC.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 비대상 시각 실행 시 집계 스킵")
    void runJaksimSamilerRankingJob_skipWhenNotLastMonday() {
        // given
        setFixedClock(LocalDateTime.of(2026, 3, 23, 2, 0));

        // when
        jaksimSamilerRankingJobScheduler.runJaksimSamilerRankingJob();

        // then
        assertThat(rankingTopicRoundJpaRepository.findAll()).isEmpty();
        assertThat(rankingEntryJpaRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS] 기존 라운드가 있으면 다음 시퀀스 라운드 생성 및 previousRank 반영")
    void runJaksimSamilerRankingJob_nextRoundAndPreviousRank_success() {
        // given
        Member memberB = createExtraMember("jaksim-batch-next-b");

        RankingTopicRound round1 = rankingTopicRoundJpaRepository.save(RankingTopicRound.of(
                rankingTopic, 1L, LocalDateTime.of(2026, 3, 1, 0, 0), LocalDateTime.of(2026, 3, 29, 23, 59, 59)));
        rankingEntryJpaRepository.saveAll(List.of(
                RankingEntry.of(round1, requestMember.getId(), 2L, null),
                RankingEntry.of(round1, memberB.getId(), 1L, null)));
        rankingTopic.updateCurrentRound(round1);
        rankingTopicJpaRepository.save(rankingTopic);

        createFailTask(
                requestMember.getId(),
                LocalDate.of(2026, 4, 10),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 10, 8, 0));
        createFailTask(
                requestMember.getId(),
                LocalDate.of(2026, 4, 12),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 12, 8, 0));
        createFailTask(
                memberB.getId(), LocalDate.of(2026, 4, 11), LocalTime.of(9, 0), LocalDateTime.of(2026, 4, 11, 8, 0));

        // when
        setFixedClock(LocalDateTime.of(2026, 4, 27, 2, 0));
        jaksimSamilerRankingJobScheduler.runJaksimSamilerRankingJob();

        // then
        Long currentRoundId = ((Number) entityManager
                        .createNativeQuery("SELECT current_round_id FROM ranking_topic WHERE id = :topicId")
                        .setParameter("topicId", rankingTopic.getId())
                        .getSingleResult())
                .longValue();
        RankingTopicRound currentRound =
                rankingTopicRoundJpaRepository.findById(currentRoundId).orElseThrow();
        assertThat(currentRound.getRound()).isEqualTo(2L);

        List<RankingEntry> round2Entries =
                rankingEntryJpaRepository
                        .findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(rankingTopic.getId(), 2L)
                        .stream()
                        .sorted(Comparator.comparing(RankingEntry::getCurrentRank))
                        .toList();
        assertThat(round2Entries).hasSize(2);
        assertThat(round2Entries.get(0).getMemberId()).isEqualTo(requestMember.getId());
        assertThat(round2Entries.get(0).getPreviousRank()).isEqualTo(2L);
        assertThat(round2Entries.get(1).getMemberId()).isEqualTo(memberB.getId());
        assertThat(round2Entries.get(1).getPreviousRank()).isEqualTo(1L);
    }

    private Member createExtraMember(String nickname) {
        Member member = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .type(MemberType.USER)
                .nickname(nickname)
                .build());
        extraMembers.add(member);
        return member;
    }

    // FAIL task 생성만 직접 주입: 상태 전이를 테스트 목적에 맞게 강제한다.
    private void createFailTask(String memberId, LocalDate date, LocalTime startTime, LocalDateTime createdAtClock) {
        transactionTemplate.executeWithoutResult(status -> entityManager
                .createNativeQuery(
                        "INSERT INTO dowith_task (member_id, task_category_id, title, status, date, start_time, "
                                + "success_at, complete_at, dowith_task_routine_id, created_at, created_by, updated_at, updated_by) "
                                + "VALUES (:memberId, NULL, :title, :status, :date, :startTime, NULL, NULL, NULL, :createdAt, :createdBy, :updatedAt, :updatedBy)")
                .setParameter("memberId", memberId)
                .setParameter("title", "jaksim-batch-task")
                .setParameter("status", DowithTaskStatus.FAIL.code)
                .setParameter("date", date)
                .setParameter("startTime", startTime)
                .setParameter("createdAt", createdAtClock)
                .setParameter("createdBy", "system")
                .setParameter("updatedAt", createdAtClock)
                .setParameter("updatedBy", "system")
                .executeUpdate());
    }
}
