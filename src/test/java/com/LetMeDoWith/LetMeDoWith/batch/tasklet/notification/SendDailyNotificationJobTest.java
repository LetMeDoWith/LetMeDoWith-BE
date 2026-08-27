package com.LetMeDoWith.LetMeDoWith.batch.tasklet.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class SendDailyNotificationJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @Qualifier("dailyMorningNotificationJob")
    private Job dailyMorningNotificationJob;

    @Autowired
    @Qualifier("dailyEveningNotificationJob")
    private Job dailyEveningNotificationJob;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private NotificationJpaRepository notificationJpaRepository;

    @Autowired
    private NotificationTemplateJpaRepository notificationTemplateJpaRepository;

    @AfterEach
    void tearDown() {
        SystemTimeUtil.resetClock();
        notificationJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
        notificationTemplateJpaRepository.deleteAll();
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

    private void seedTemplate(NotificationTemplateCode code) {
        notificationTemplateJpaRepository.save(
                NotificationTemplate.of(code, NotificationType.NORMAL, "테스트 제목", "테스트 본문", "letmedowith://home"));
    }

    private Member createMember(String nickname, MemberStatus status) {
        return memberJpaRepository.save(Member.builder()
                .status(status)
                .nickname(nickname)
                .selfDescription("test description")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1995, 11, 4))
                .type(MemberType.USER)
                .build());
    }

    @Test
    void dailyMorningNotificationJob_NORMAL_회원만_발송_대상으로_조회되고_잡은_정상_완료된다() throws Exception {
        // given
        seedTemplate(NotificationTemplateCode.DAILY_MON_AM);
        Member normalMember = createMember("normal", MemberStatus.NORMAL);
        createMember("withdrawn", MemberStatus.WITHDRAWN);
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 9, 0, 0); // 2026-01-05 == 월요일
        setFixedClock(executionDateTime);

        // when
        jobLauncherTestUtils.setJob(dailyMorningNotificationJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        List<Member> targetedMembers = memberJpaRepository.findAllByStatusIn(List.of(MemberStatus.NORMAL));
        assertThat(targetedMembers).extracting(Member::getId).containsExactly(normalMember.getId());
    }

    @Test
    void dailyEveningNotificationJob_NORMAL_회원이_없으면_알림_발송_없이_정상_완료된다() throws Exception {
        // given
        createMember("withdrawn", MemberStatus.WITHDRAWN);
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 22, 0, 0);
        setFixedClock(executionDateTime);

        // when
        jobLauncherTestUtils.setJob(dailyEveningNotificationJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findAll()).isEmpty();
    }

    @Test
    void dailyEveningNotificationJob_알림_토큰이_없는_NORMAL_회원은_실패로_집계되지만_잡은_정상_완료된다() throws Exception {
        // given
        // 알림 토큰(NotificationToken)을 등록하지 않아 실제 FCM 발송 없이도, 잡 자체의 정상 동작(회원 조회 ~
        // NotificationSendService 호출까지)을 검증할 수 있다.
        seedTemplate(NotificationTemplateCode.DAILY_TUES_PM);
        createMember("normal", MemberStatus.NORMAL);
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 6, 22, 0, 0); // 2026-01-06 == 화요일
        setFixedClock(executionDateTime);

        // when
        jobLauncherTestUtils.setJob(dailyEveningNotificationJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(notificationJpaRepository.findAll()).isEmpty();
    }
}
