package com.LetMeDoWith.LetMeDoWith.batch.tasklet;

import com.LetMeDoWith.LetMeDoWith.batch.dto.DowithTaskWithFeedbackCountDto;
import com.LetMeDoWith.LetMeDoWith.common.cache.CacheHelper;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class StoreFeedbackAvailableDowithAndLazyDowithMemberTasklet implements Tasklet {

    private static final RowMapper<DowithTaskWithFeedbackCountDto> mapper = ((rs, rowNum) -> {
        return new DowithTaskWithFeedbackCountDto(
            rs.getLong("id"),
            rs.getString("memberId"),
            rs.getString("nickname"),
            rs.getString("badgeImageUrl"),
            rs.getString("title"),
            rs.getString("status"),
            rs.getTime("startTime").toLocalTime(),
            rs.getLong("feedbackCount"));
    });

    private final JdbcTemplate jdbcTemplate;
    private final CacheHelper cacheHelper;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        LocalDateTime targetDateTime = executionDateTime;

        if (executionDateTime == null) {
            targetDateTime = LocalDateTime.now();
        }

        LocalDate standardDate = targetDateTime.toLocalDate();

        // 잔소리 대상 두윗 조회
        String selectFeedbackAvailableDowithListQuery =
            """
                SELECT
                    t.id,
                    t.member_id,
                    m.nickname AS memberNickname,
                    t.title,
                    t.status,
                    t.start_time,
                
                    (
                        SELECT COUNT(*)
                        FROM dowith_task_feedback f
                        WHERE f.dowith_task_id = t.id
                    ) AS feedbackCount,
                
                    b.image_url AS badgeImageUrl
                
                FROM dowith_task t
                
                LEFT JOIN member m
                    ON m.id = t.member_id
                
                LEFT JOIN member_badge mb
                    ON mb.member_id = m.id
                   AND mb.main_yn = 'Y'
                
                LEFT JOIN badge b
                    ON b.id = mb.badge_id
                
                WHERE t.status = ?
                  AND t.date = ?
                  AND TIMESTAMP(t.date, t.start_time) <= NOW()
                  AND NOW() < TIMESTAMP(t.date, t.start_time) + INTERVAL 59 MINUTE
                
                ORDER BY TIMESTAMP(t.date, t.start_time) desc
                """;

        List<DowithTaskWithFeedbackCountDto> dowithTaskList = jdbcTemplate.query(
            selectFeedbackAvailableDowithListQuery, mapper, DowithTaskStatus.WAIT.code,
            standardDate);

        // 레이지 두윗러 목록 계산
        List<Long> lazyDowithIdList = dowithTaskList.stream()
            .map(DowithTaskWithFeedbackCountDto::id)
            .limit(15)
            .toList();

        // 레이지 두윗러 및 잔소리 대상 두윗 캐시 적재
        // cacheHelper.push();

        return RepeatStatus.FINISHED;
    }
}