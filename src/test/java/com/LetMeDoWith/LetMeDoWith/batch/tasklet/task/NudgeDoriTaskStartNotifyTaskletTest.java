package com.LetMeDoWith.LetMeDoWith.batch.tasklet.task;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTokenJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class NudgeDoriTaskStartNotifyTaskletTest {

    // TODO - 테스트 FCM 토큰 generator에서 발급 받은 토큰 세팅
    private static final String REGISTERED_FCM_TOKEN =
            "fx5STrP_eh7XIRNiVvNBk_:APA91bHpJ_SvZQTs8SK-Hkl5d8vChDEb2_njBRp-uLtzWU-3_s5W9aoL6OprShJG-ZIU4oSSDD4cfvB0jKb8xUcjvLWyVvhDkiM9DhsdrxhKa0wwrDwx-YI";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job nudgeDoriTaskStartJob;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    private NotificationTemplateJpaRepository notificationTemplateJpaRepository;

    @Autowired
    private NotificationTokenJpaRepository notificationTokenJpaRepository;

    @Autowired
    private NotificationJpaRepository notificationJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Member member;
    private NotificationToken notificationToken;
    private NotificationTemplate notificationTemplate;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(nudgeDoriTaskStartJob);

        member = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("test")
                .selfDescription("test description")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1995, 11, 4))
                .type(MemberType.USER)
                .build());
        notificationToken =
                notificationTokenJpaRepository.save(NotificationToken.of(member.getId(), REGISTERED_FCM_TOKEN));
        notificationTemplate = notificationTemplateJpaRepository.save(NotificationTemplate.of(
                NotificationTemplateCode.NUDGE_DORI_START,
                NotificationType.NORMAL,
                "도리 Todo 시작 10분 전이에요",
                "{{dowithTaskTitle}} 시작이 10분 남았어요!",
                "letmedowith://dowith"));
    }

    @AfterEach
    void tearDown() {
        SystemTimeUtil.resetClock();
        notificationJpaRepository.deleteAll();
        dowithTaskJpaRepository.deleteAll();
        notificationTokenJpaRepository.delete(notificationToken);
        notificationTemplateJpaRepository.delete(notificationTemplate);
        memberJpaRepository.delete(member);
    }

    private void setFixedClock(LocalDateTime dateTime) {
        SystemTimeUtil.setClock(Clock.fixed(dateTime.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
    }

    private JobParameters jobParameters(LocalDateTime executionDateTime) {
        return new JobParametersBuilder()
                .addLong("run.id", System.nanoTime())
                .addLocalDateTime("executionDateTime", executionDateTime)
                .toJobParameters();
    }

    @Test
    @DisplayName("[SUCCESS] 시작까지 10분 남은 WAIT 상태의 DowithTask에 알림을 보낸다")
    void nudgeDoriTaskStart_success() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 1, 9, 0, 0);
        LocalDateTime targetStartDateTime = executionDateTime.plusMinutes(12); // 09:12 -> [09:10, 09:15) 윈도우 내

        setFixedClock(executionDateTime);
        dowithTaskJpaRepository.save(DowithTask.of(
                member.getId(), null, "아침 도리", targetStartDateTime.toLocalDate(), targetStartDateTime.toLocalTime()));

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Optional<Notification> savedNotification = notificationJpaRepository.findByMemberId(member.getId());
        assertThat(savedNotification).isPresent();
        assertThat(savedNotification.get().getBody()).isEqualTo("아침 도리 시작이 10분 남았어요!");
        assertThat(savedNotification.get().getNotificationTemplateCode())
                .isEqualTo(NotificationTemplateCode.NUDGE_DORI_START.getCode());
    }

    @Test
    @DisplayName("[FAIL] 시작까지 10분 이상 남은 DowithTask는 알림 대상에서 제외된다")
    void nudgeDoriTaskStart_outsideWindow_notNotified() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 1, 9, 0, 0);
        LocalDateTime notInWindowStartDateTime = executionDateTime.plusMinutes(30); // 09:30 -> 윈도우 밖

        setFixedClock(executionDateTime);
        dowithTaskJpaRepository.save(DowithTask.of(
                member.getId(),
                null,
                "점심 도리",
                notInWindowStartDateTime.toLocalDate(),
                notInWindowStartDateTime.toLocalTime()));

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("[FAIL] 이미 완료(SUCCESS)된 DowithTask는 알림 대상에서 제외된다")
    void nudgeDoriTaskStart_alreadySuccess_notNotified() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 1, 9, 0, 0);
        LocalDateTime targetStartDateTime = executionDateTime.plusMinutes(12);

        setFixedClock(executionDateTime);
        DowithTask completedDowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                member.getId(),
                null,
                "이미 끝난 도리",
                targetStartDateTime.toLocalDate(),
                targetStartDateTime.toLocalTime()));
        // DowithTask.success()는 인증 이미지 업로드 등 별도 전제 조건이 있어, 테스트에서는 상태만 직접 갱신한다.
        jdbcTemplate.update("UPDATE dowith_task SET status = ? WHERE id = ?", "SUCCESS", completedDowithTask.getId());

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS] 윈도우가 자정을 넘어가도 다음날 DowithTask를 정상적으로 조회해 알림을 보낸다")
    void nudgeDoriTaskStart_midnightRollover_success() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 1, 23, 48, 0);
        // 윈도우: [실행시각+10분, 실행시각+15분) = [2026-01-01 23:58, 2026-01-02 00:03) - 날짜가 갈리는 구간
        LocalDateTime targetStartDateTime = LocalDateTime.of(2026, 1, 2, 0, 1, 0);

        setFixedClock(executionDateTime);
        dowithTaskJpaRepository.save(DowithTask.of(
                member.getId(),
                null,
                "자정 넘어 시작하는 도리",
                targetStartDateTime.toLocalDate(),
                targetStartDateTime.toLocalTime()));

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Optional<Notification> savedNotification = notificationJpaRepository.findByMemberId(member.getId());
        assertThat(savedNotification).isPresent();
        assertThat(savedNotification.get().getBody()).isEqualTo("자정 넘어 시작하는 도리 시작이 10분 남았어요!");
    }
}
