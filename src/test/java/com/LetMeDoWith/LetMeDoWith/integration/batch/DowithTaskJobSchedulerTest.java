package com.LetMeDoWith.LetMeDoWith.integration.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.batch.scheduler.DowithTaskJobScheduler;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DowithTaskJobSchedulerTest extends AbstractIntegrationTest {

    @Autowired
    private DowithTaskJobScheduler dowithTaskJobScheduler;

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Override
    protected void deleteTestData() {}

    @Override
    protected void createTestData() {
        List<DowithTask> tasks = new ArrayList<>();
        this.setFixedClock(LocalDateTime.of(2025, 2, 20, 10, 0));
        tasks.add(
                DowithTask.of(requestMember.getId(), null, "테스트 테스크", LocalDate.of(2025, 2, 20), LocalTime.of(11, 0)));

        this.setFixedClock(LocalDateTime.of(2025, 3, 1, 10, 0));

        // 20:00 ~ 21:00 사이에 10분 간격으로 DowithTask 등록
        for (int i = 0; i <= 6; i++) {
            int hour = 20;
            int minutes = i * 10;

            if (minutes == 60) {
                hour = 21;
                minutes = 0;
            }

            DowithTask dowithTask = DowithTask.of(
                    requestMember.getId(), null, "테스트 테스크 " + i, LocalDate.of(2025, 3, 1), LocalTime.of(hour, minutes));
            tasks.add(dowithTask);
        }
        dowithTaskJpaRepository.saveAllAndFlush(tasks);
    }

    @Test
    void testFailDowithTaskJob() {
        // given
        this.setFixedClock(LocalDateTime.of(2025, 3, 1, 21, 30));

        // when
        dowithTaskJobScheduler.runFailTaskJob();

        // then
        List<DowithTask> dowithTasks = dowithTaskJpaRepository.findAll();
        for (DowithTask task : dowithTasks) {
            if (task.getDate().isBefore(LocalDate.of(2025, 3, 1))) {
                assertThat(task.getStatus()).isEqualTo(DowithTaskStatus.FAIL);
                continue;
            }

            if (task.getStartTime().isBefore(LocalTime.of(20, 30))
                    || task.getStartTime().equals(LocalTime.of(20, 30))) {
                assertThat(task.getStatus()).isEqualTo(DowithTaskStatus.FAIL);
            } else {
                assertThat(task.getStatus()).isEqualTo(DowithTaskStatus.WAIT);
            }
        }
    }
}
