package com.LetMeDoWith.LetMeDoWith.integration.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.batch.scheduler.RankingJobScheduler;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
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

class GodsaengSilcheonreoRankingJobSchedulerIntegrationTest extends AbstractIntegrationTest {

    private static final String GODSAENG_TOPIC_TITLE = "갓생실천러";

    @Autowired
    private RankingJobScheduler rankingJobScheduler;

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

    private RankingTopic godsaengTopic;
    private final List<Member> extraMembers = new ArrayList<>();

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
        godsaengTopic = rankingTopicJpaRepository.save(RankingTopic.builder()
                .title(GODSAENG_TOPIC_TITLE)
                .description("test")
                .build());
    }

    @Test
    @DisplayName("[SUCCESS] 마지막 주 월요일 02시 실행 시 성공 비율 기준으로 1회차 엔트리 생성")
    void runGodsaengSilcheonreoRankingJob_firstRound_success() {
        // given
        Member memberB = createExtraMember("godsaeng-batch-b");
        Member memberC = createExtraMember("godsaeng-batch-c");

        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 10),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 10, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 12),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 12, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 14),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 14, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 16),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 16, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 18),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 18, 8, 0));

        createTask(
                memberB.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 5),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 5, 7, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 6),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 6, 7, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 7),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 7, 7, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 9),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 9, 7, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 11),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 11, 7, 0));

        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 8),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 8, 7, 0));
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 10),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 10, 7, 0));
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 12),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 12, 7, 0));
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 14),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 14, 7, 0));
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 16),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 16, 7, 0));

        // when
        setFixedClock(LocalDateTime.of(2026, 3, 30, 2, 0));
        rankingJobScheduler.runGodsaengSilcheonreoRankingJob();

        // then
        entityManager.clear();
        RankingTopicRound currentRound = rankingTopicRoundJpaRepository
                .findByRankingTopicIdAndRound(godsaengTopic.getId(), 1L)
                .orElseThrow();

        assertThat(currentRound.getRound()).isEqualTo(1L);
        assertThat(currentRound.getAggregationStartDateTime()).isEqualTo(LocalDateTime.of(2026, 3, 1, 0, 0));
        assertThat(currentRound.getAggregationEndDateTime()).isEqualTo(LocalDateTime.of(2026, 3, 29, 23, 59, 59));

        List<RankingEntry> entries =
                rankingEntryJpaRepository
                        .findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(godsaengTopic.getId(), 1L)
                        .stream()
                        .sorted(Comparator.comparing(RankingEntry::getCurrentRank))
                        .toList();

        assertThat(entries).hasSize(3);
        assertThat(entries.get(0).getMemberId()).isEqualTo(requestMember.getId());
        assertThat(entries.get(0).getPreviousRank()).isNull();
        assertThat(entries.get(1).getMemberId()).isEqualTo(memberC.getId());
        assertThat(entries.get(2).getMemberId()).isEqualTo(memberB.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 성공 비율을 우선 적용하고 동일 비율이면 성공 수로 순위를 결정한다")
    void runGodsaengSilcheonreoRankingJob_rankBySuccessRateAndSuccessCount() {
        // given
        Member memberB = createExtraMember("godsaeng-batch-rate-b");
        Member memberC = createExtraMember("godsaeng-batch-rate-c");

        // requestMember: 성공 2건 / 전체 6건
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 10),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 10, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 11),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 11, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 12),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 12, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 13),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 13, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 14),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 14, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 3, 15),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 15, 8, 0));

        // memberB: 성공 5건 / 전체 5건
        createTask(
                memberB.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 16),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 16, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 17),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 17, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 18),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 18, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 19),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 19, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 20),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 20, 8, 0));

        // memberC: 성공 4건 / 전체 4건 -> 집계 대상 제외
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 21),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 21, 8, 0));
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 22),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 22, 8, 0));
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 23),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 23, 8, 0));
        createTask(
                memberC.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 3, 24),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 3, 24, 8, 0));

        // when
        setFixedClock(LocalDateTime.of(2026, 3, 30, 2, 0));
        rankingJobScheduler.runGodsaengSilcheonreoRankingJob();

        // then
        entityManager.clear();
        List<RankingEntry> entries =
                rankingEntryJpaRepository
                        .findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(godsaengTopic.getId(), 1L)
                        .stream()
                        .sorted(Comparator.comparing(RankingEntry::getCurrentRank))
                        .toList();

        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).getMemberId()).isEqualTo(memberB.getId());
        assertThat(entries.get(1).getMemberId()).isEqualTo(requestMember.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 기존 라운드가 있으면 성공 비율 순위와 previousRank를 반영한다")
    void runGodsaengSilcheonreoRankingJob_nextRoundAndPreviousRank_success() {
        // given
        Member memberB = createExtraMember("godsaeng-batch-next-b");

        RankingTopicRound round1 = rankingTopicRoundJpaRepository.save(RankingTopicRound.of(
                godsaengTopic, 1L, LocalDateTime.of(2026, 3, 1, 0, 0), LocalDateTime.of(2026, 3, 29, 23, 59, 59)));
        rankingEntryJpaRepository.saveAll(List.of(
                RankingEntry.of(round1, requestMember.getId(), 2L, null),
                RankingEntry.of(round1, memberB.getId(), 1L, null)));
        godsaengTopic.updateCurrentRound(round1);
        rankingTopicJpaRepository.save(godsaengTopic);

        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 4, 10),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 10, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 4, 12),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 12, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 4, 14),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 14, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 4, 16),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 16, 8, 0));
        createTask(
                requestMember.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 4, 18),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 18, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.SUCCESS,
                LocalDate.of(2026, 4, 11),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 11, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 4, 13),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 13, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 4, 15),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 15, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 4, 17),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 17, 8, 0));
        createTask(
                memberB.getId(),
                DowithTaskStatus.FAIL,
                LocalDate.of(2026, 4, 19),
                LocalTime.of(9, 0),
                LocalDateTime.of(2026, 4, 19, 8, 0));

        // when
        setFixedClock(LocalDateTime.of(2026, 4, 27, 2, 0));
        rankingJobScheduler.runGodsaengSilcheonreoRankingJob();

        // then
        entityManager.clear();
        RankingTopicRound currentRound = rankingTopicRoundJpaRepository
                .findByRankingTopicIdAndRound(godsaengTopic.getId(), 2L)
                .orElseThrow();

        assertThat(currentRound.getRound()).isEqualTo(2L);

        List<RankingEntry> entries =
                rankingEntryJpaRepository
                        .findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(godsaengTopic.getId(), 2L)
                        .stream()
                        .sorted(Comparator.comparing(RankingEntry::getCurrentRank))
                        .toList();

        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).getMemberId()).isEqualTo(requestMember.getId());
        assertThat(entries.get(0).getPreviousRank()).isEqualTo(2L);
        assertThat(entries.get(1).getMemberId()).isEqualTo(memberB.getId());
        assertThat(entries.get(1).getPreviousRank()).isEqualTo(1L);
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

    private void createTask(
            String memberId,
            DowithTaskStatus status,
            LocalDate date,
            LocalTime startTime,
            LocalDateTime createdAtDateTime) {
        transactionTemplate.executeWithoutResult(transactionStatus -> this.entityManager
                .createNativeQuery(
                        "INSERT INTO dowith_task (member_id, task_category_id, title, status, date, start_time, "
                                + "success_at, complete_at, dowith_task_routine_id, created_at, created_by, updated_at, updated_by) "
                                + "VALUES (:memberId, NULL, :title, :status, :date, :startTime, :successAt, NULL, NULL, :createdAt, :createdBy, :updatedAt, :updatedBy)")
                .setParameter("memberId", memberId)
                .setParameter("title", "godsaeng-batch-task")
                .setParameter("status", status.code)
                .setParameter("date", date)
                .setParameter("startTime", startTime)
                .setParameter(
                        "successAt", status == DowithTaskStatus.SUCCESS ? LocalDateTime.of(date, startTime) : null)
                .setParameter("createdAt", createdAtDateTime)
                .setParameter("createdBy", "system")
                .setParameter("updatedAt", createdAtDateTime)
                .setParameter("updatedBy", "system")
                .executeUpdate());
    }
}
