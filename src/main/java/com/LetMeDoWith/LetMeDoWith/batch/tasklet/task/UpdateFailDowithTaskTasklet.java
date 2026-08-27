package com.LetMeDoWith.LetMeDoWith.batch.tasklet.task;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class UpdateFailDowithTaskTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        // 시작한지 1시간이 지난 DowithTask들을 FAIL로 업데이트
        LocalDateTime standardDateTime = executionDateTime.minusHours(1);

        int updatedCount = this.jdbcTemplate.update(
                """
                        UPDATE dowith_task
                            SET status = ?,
                                updated_at = ?,
                                updated_by = ?
                            WHERE status = ?
                                AND TIMESTAMP(date, start_time) <= ?
                        """,
                DowithTaskStatus.FAIL.code,
                Timestamp.valueOf(executionDateTime),
                "system",
                DowithTaskStatus.WAIT.code,
                Timestamp.valueOf(standardDateTime));

        return RepeatStatus.FINISHED;
    }
}
