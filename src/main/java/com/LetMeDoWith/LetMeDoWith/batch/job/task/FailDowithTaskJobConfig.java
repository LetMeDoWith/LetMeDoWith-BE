package com.LetMeDoWith.LetMeDoWith.batch.job.task;

import com.LetMeDoWith.LetMeDoWith.batch.dto.DowithTaskDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FailDowithTaskJobConfig {

    private static final String JOB_NAME = "failDowithTaskJob";
    private static final String READER_NAME = "failDowithTaskReader";
    private static final String PROCESSOR_NAME = "failDowithTaskProcessor";
    private static final String WRITER_NAME = "failDowithTaskWriter";

    private static final int CHUNK_SIZE = 1000;

    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job failDowithTaskJob(Step failDowithTaskStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(failDowithTaskStep)
                .build();
    }

    @Bean
    @JobScope
    public Step failDowithTaskStep(
            JdbcPagingItemReader<DowithTaskDto> failDowithTaskReader,
            ItemProcessor<DowithTaskDto, DowithTaskDto> failDowithTaskProcessor,
            JdbcBatchItemWriter<DowithTaskDto> failDowithTaskWriter) {
        return new StepBuilder("failDowithTaskStep", jobRepository)
                .<DowithTaskDto, DowithTaskDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(failDowithTaskReader)
                .processor(failDowithTaskProcessor)
                .writer(failDowithTaskWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<DowithTaskDto> failDowithTaskReader(
            @Value("#{jobParameters['executionDateTime']}") LocalDateTime executionDateTime) {

        log.info("FailDowithTaskJobConfig - executionDateTime: {}", executionDateTime);
        LocalDate standardDate = executionDateTime.toLocalDate();
        LocalTime standardTime = executionDateTime.toLocalTime().minusHours(1);

        // 절대로 order by 키가 있어야 함 (페이지네이션에서는 정렬 기준이 필수)
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        // MySqlPagingQueryProvider 설정
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause(
                "id, member_id, task_category_id, title, status, date, start_time, success_at, complete_at");
        queryProvider.setFromClause("dowith_task");
        queryProvider.setWhereClause("status = :status AND date <= :standardDate AND start_time <= :standardTime");
        queryProvider.setSortKeys(sortKeys);

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("status", DowithTaskStatus.WAIT.code);
        parameterValues.put("standardDate", standardDate);
        parameterValues.put("standardTime", standardTime);

        return new JdbcPagingItemReaderBuilder<DowithTaskDto>()
                .name(READER_NAME)
                .dataSource(dataSource)
                .pageSize(CHUNK_SIZE)
                .queryProvider(queryProvider)
                .parameterValues(parameterValues)
                //                .rowMapper((rs, rowNum) -> {
                //                    DowithTaskDto dto = new DowithTaskDto();
                //                    dto.setId(rs.getLong("id"));
                //                    dto.setMemberId(rs.getString("member_id"));
                //                    dto.setTaskCategoryId(rs.getLong("task_category_id"));
                //                    dto.setTitle(rs.getString("title"));
                //                    dto.setStatus(rs.getString("status"));
                //                    dto.setDate(rs.getDate("date").toLocalDate());
                //                    dto.setStartTime(rs.getTime("start_time").toLocalTime());
                //                    dto.setSuccessAt(rs.getTimestamp("success_at") != null
                //                            ? rs.getTimestamp("success_at").toLocalDateTime()
                //                            : null);
                //                    dto.setCompleteAt(rs.getTimestamp("complete_at") != null
                //                            ? rs.getTimestamp("complete_at").toLocalDateTime()
                //                            : null);
                //                    return dto;
                //                })
                .rowMapper(new BeanPropertyRowMapper<>(DowithTaskDto.class))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<DowithTaskDto, DowithTaskDto> failDowithTaskProcessor() {
        return dowithTaskDto -> {
            // 처리 로직 구현 (예: 상태 변경, 알림 전송 등)
            // 여기서는 단순히 로그 출력
            dowithTaskDto.setStatus(DowithTaskStatus.FAIL.code);
            return dowithTaskDto;
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<DowithTaskDto> failDowithTaskWriter(
            @Value("#{jobParameters['executionDateTime']}") LocalDateTime executionDateTime) {
        return new JdbcBatchItemWriterBuilder<DowithTaskDto>()
                .dataSource(dataSource)
                .sql("UPDATE dowith_task SET status = ?, updated_at = ?, updated_by = ? WHERE id = ?")
                .itemPreparedStatementSetter((dto, ps) -> {
                    ps.setString(1, dto.getStatus());
                    ps.setTimestamp(2, Timestamp.valueOf(executionDateTime));
                    ps.setString(3, "system"); // 시스템 작업자
                    ps.setLong(4, dto.getId());
                })
                .build();
    }
}
