package com.LetMeDoWith.LetMeDoWith.batch.tasklet.notification.task;

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
import java.time.LocalTime;
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
class FailDoriTaskNotifyTaskletTest {

    // TODO - 테스트 FCM 토큰 generator에서 발급 받은 토큰 세팅
    private static final String REGISTERED_FCM_TOKEN =
            "fx5STrP_eh7XIRNiVvNBk_:APA91bHpJ_SvZQTs8SK-Hkl5d8vChDEb2_njBRp-uLtzWU-3_s5W9aoL6OprShJG-ZIU4oSSDD4cfvB0jKb8xUcjvLWyVvhDkiM9DhsdrxhKa0wwrDwx-YI";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job doriTaskFailNotifyJob;

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
        jobLauncherTestUtils.setJob(doriTaskFailNotifyJob);

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
                NotificationTemplateCode.DORI_FAIL,
                NotificationType.NORMAL,
                "잡도리 실패",
                "{{doriTaskTitle}} 결국 못함.. 잡도리 더 필요하구나?",
                "letmedowith://home"));
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

    /**
     * 검증 대상 Task를 등록한다. validateStartDateTime()을 통과시키기 위해, task의 시작 날짜보다 하루 이른 시각을
     * 생성 시점 clock으로 세팅한 뒤 등록하고, 실제 배치 실행 시각(executionDateTime)으로 clock을 되돌린다.
     */
    private DowithTask createTask(String title, LocalDate date, LocalTime startTime, LocalDateTime executionDateTime) {
        setFixedClock(date.minusDays(1).atStartOfDay());
        DowithTask task = dowithTaskJpaRepository.save(DowithTask.of(member.getId(), null, title, date, startTime));
        setFixedClock(executionDateTime);
        return task;
    }

    @Test
    @DisplayName("[SUCCESS] 시작한지 1시간 넘은 WAIT 상태의 DowithTask에 실패 알림을 보낸다")
    void failDoriTaskNotify_success() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 12, 0, 0);
        DowithTask task = createTask("테스트 도리", LocalDate.of(2026, 1, 5), LocalTime.of(10, 0, 0), executionDateTime);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Optional<Notification> savedNotification = notificationJpaRepository.findByMemberId(member.getId());
        assertThat(savedNotification).isPresent();
        assertThat(savedNotification.get().getBody()).isEqualTo("테스트 도리 결국 못함.. 잡도리 더 필요하구나?");
        assertThat(savedNotification.get().getNotificationTemplateCode())
                .isEqualTo(NotificationTemplateCode.DORI_FAIL.getCode());
    }

    @Test
    @DisplayName("[SUCCESS] 경계값 - 정확히 1시간 지난 시점(포함)의 WAIT Task에도 실패 알림을 보낸다")
    void failDoriTaskNotify_exactlyOneHourBoundary_inclusive() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 2, 0, 10, 0);
        DowithTask task = createTask("경계값 도리", LocalDate.of(2026, 1, 1), LocalTime.of(23, 10, 0), executionDateTime);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findByMemberId(member.getId())).isPresent();
    }

    @Test
    @DisplayName("[FAIL] 시작한지 1시간이 안 지난 WAIT Task는 알림 대상에서 제외된다")
    void failDoriTaskNotify_underOneHour_notNotified() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 12, 0, 0);
        DowithTask task = createTask("아직 안지남 도리", LocalDate.of(2026, 1, 5), LocalTime.of(11, 30, 0), executionDateTime);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("[FAIL] 이미 SUCCESS 처리된 DowithTask는 알림 대상에서 제외된다")
    void failDoriTaskNotify_alreadySuccess_notNotified() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 12, 0, 0);
        DowithTask task = createTask("이미 끝난 도리", LocalDate.of(2026, 1, 5), LocalTime.of(10, 0, 0), executionDateTime);
        jdbcTemplate.update("UPDATE dowith_task SET status = ? WHERE id = ?", "SUCCESS", task.getId());

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("[FAIL] 이미 FAIL 처리된 DowithTask는 알림 대상에서 제외된다")
    void failDoriTaskNotify_alreadyFail_notNotified() throws Exception {
        // given
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 12, 0, 0);
        DowithTask task =
                createTask("이미 실패 처리된 도리", LocalDate.of(2026, 1, 5), LocalTime.of(10, 0, 0), executionDateTime);
        jdbcTemplate.update("UPDATE dowith_task SET status = ? WHERE id = ?", "FAIL", task.getId());

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findByMemberId(member.getId())).isEmpty();
    }
}
