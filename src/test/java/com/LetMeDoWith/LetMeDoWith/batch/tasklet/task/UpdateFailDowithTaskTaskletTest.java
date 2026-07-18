package com.LetMeDoWith.LetMeDoWith.batch.tasklet.task;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
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
class UpdateFailDowithTaskTaskletTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job failDowithTaskJob;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Member member;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(failDowithTaskJob);

        member = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("test")
                .selfDescription("test description")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1995, 11, 4))
                .type(MemberType.USER)
                .build());
    }

    @AfterEach
    void tearDown() {
        SystemTimeUtil.resetClock();
        dowithTaskJpaRepository.deleteAll();
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
    private DowithTask createTask(LocalDate date, LocalTime startTime, LocalDateTime executionDateTime) {
        setFixedClock(date.minusDays(1).atStartOfDay());
        DowithTask task = dowithTaskJpaRepository.save(DowithTask.of(member.getId(), null, "테스트 두윗", date, startTime));
        setFixedClock(executionDateTime);
        return task;
    }

    private DowithTaskStatus statusOf(DowithTask task) {
        return dowithTaskJpaRepository.findById(task.getId()).orElseThrow().getStatus();
    }

    @Test
    @DisplayName("[SUCCESS] 평시(자정 아님) - 시작한지 1시간 넘은 WAIT Task는 FAIL 처리된다")
    void failsTask_overOneHour_normalDay() throws Exception {
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 12, 0, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 5), LocalTime.of(10, 0, 0), executionDateTime);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.FAIL);
    }

    @Test
    @DisplayName("[SUCCESS] 평시(자정 아님) - 시작한지 1시간 안 지난 WAIT Task는 그대로 WAIT 유지된다")
    void keepsWait_underOneHour_normalDay() throws Exception {
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 12, 0, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 5), LocalTime.of(11, 30, 0), executionDateTime);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.WAIT);
    }

    @Test
    @DisplayName("[SUCCESS] 자정 경과 - 전날 밤에 시작해 1시간 넘게 지난 WAIT Task는 FAIL 처리된다")
    void failsTask_overOneHour_acrossMidnight() throws Exception {
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 2, 0, 10, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 1), LocalTime.of(22, 0, 0), executionDateTime);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.FAIL);
    }

    @Test
    @DisplayName("[SUCCESS] 자정 경과 - 전날 밤에 시작했지만 아직 1시간 안 지난 WAIT Task는 그대로 WAIT 유지된다")
    void keepsWait_underOneHour_acrossMidnight_previousDay() throws Exception {
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 2, 0, 10, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 1), LocalTime.of(23, 40, 0), executionDateTime);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.WAIT);
    }

    @Test
    @DisplayName("[SUCCESS][회귀] 자정 직후 - 오늘 새벽에 막 시작한(1시간 안 지난) WAIT Task는 FAIL 처리되면 안 된다")
    void keepsWait_justStartedToday_rightAfterMidnight() throws Exception {
        // 과거 버그: date/start_time을 따로 비교하면서 standardTime이 LocalTime만으로 계산되어
        // 자정 직후엔 (nowTime - 1h)가 전날 늦은 시각으로 wrap 되고, 이 값과 오늘 새벽 startTime을 비교해
        // 방금 시작한 Task까지 FAIL 처리되던 케이스.
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 2, 0, 10, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 2), LocalTime.of(0, 5, 0), executionDateTime);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.WAIT);
    }

    @Test
    @DisplayName("[SUCCESS][회귀] 자정 직후 - 오늘 아직 시작하지 않은 WAIT Task는 FAIL 처리되면 안 된다")
    void keepsWait_futureStartToday_rightAfterMidnight() throws Exception {
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 2, 0, 10, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 2), LocalTime.of(8, 0, 0), executionDateTime);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.WAIT);
    }

    @Test
    @DisplayName("[SUCCESS] 경계값 - 정확히 1시간 지난 시점(포함)의 WAIT Task는 FAIL 처리된다")
    void failsTask_exactlyOneHourBoundary_inclusive() throws Exception {
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 2, 0, 10, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 1), LocalTime.of(23, 10, 0), executionDateTime);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.FAIL);
    }

    @Test
    @DisplayName("[FAIL] 이미 SUCCESS 처리된 Task는 대상에서 제외되어 상태가 유지된다")
    void ignoresAlreadySuccessTask() throws Exception {
        LocalDateTime executionDateTime = LocalDateTime.of(2026, 1, 5, 12, 0, 0);
        DowithTask task = createTask(LocalDate.of(2026, 1, 5), LocalTime.of(10, 0, 0), executionDateTime);
        jdbcTemplate.update("UPDATE dowith_task SET status = ? WHERE id = ?", "SUCCESS", task.getId());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters(executionDateTime));

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(statusOf(task)).isEqualTo(DowithTaskStatus.SUCCESS);
    }
}
