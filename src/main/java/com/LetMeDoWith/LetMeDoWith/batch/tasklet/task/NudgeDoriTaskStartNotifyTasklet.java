package com.LetMeDoWith.LetMeDoWith.batch.tasklet.task;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.SendNotificationResult;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationSendService;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class NudgeDoriTaskStartNotifyTasklet implements Tasklet {

    private static final int LOOKAHEAD_MINUTES = 10;
    private static final int JOB_INTERVAL_MINUTES = 5;

    private final JdbcTemplate jdbcTemplate;

    private final NotificationSendService notificationSendService;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        // 시작 시각이 (실행 시각 + 10분)인 DowithTask들의 dowithTaskId, dowithTaskTitle, member_id 조회하기
        // 배치가 5분 간격으로 실행되므로 [실행시각+10분, 실행시각+15분) 구간을 하나의 윈도우로 매칭해
        // 다음 실행 전까지 놓치는 구간 없이 정확히 한 번씩만 알림이 발송되도록 한다.
        LocalDateTime windowStart =
                executionDateTime.plusMinutes(LOOKAHEAD_MINUTES).withSecond(0).withNano(0);
        LocalDateTime windowEnd = windowStart.plusMinutes(JOB_INTERVAL_MINUTES).minusSeconds(1);

        List<NudgeDoriTaskTarget> nudgeTargets = findNudgeTargets(windowStart, windowEnd);

        if (nudgeTargets.isEmpty()) {
            return RepeatStatus.FINISHED;
        }

        List<String> receiverMemberIds =
                nudgeTargets.stream().map(NudgeDoriTaskTarget::memberId).toList();
        List<Map<String, String>> bodyParams = nudgeTargets.stream()
                .map(target -> Map.of(
                        "dowithTaskId", String.valueOf(target.dowithTaskId()),
                        "dowithTaskTitle", target.dowithTaskTitle()))
                .toList();

        SendNotificationResult sendNotificationResult = notificationSendService.sendNotifications(
                NotificationTemplateCode.NUDGE_DORI_START, receiverMemberIds, null, bodyParams, null);

        if (!sendNotificationResult.failedMemberIds().isEmpty()) {
            for (NudgeDoriTaskTarget target : nudgeTargets) {
                if (sendNotificationResult.failedMemberIds().contains(target.memberId())) {
                    log.error(
                            "도리 Todo 시작 재촉 알림 발송 실패 - memberId: {}, dowithTaskId: {}",
                            target.memberId(),
                            target.dowithTaskId());
                }
            }
        }

        return RepeatStatus.FINISHED;
    }

    /**
     * windowStart ~ windowEnd 구간의 DowithTask를 조회한다.
     * date와 start_time이 분리된 컬럼이라, 구간이 자정을 넘어가면 날짜별로 나눠 조회한 뒤 합친다.
     */
    private List<NudgeDoriTaskTarget> findNudgeTargets(LocalDateTime windowStart, LocalDateTime windowEnd) {

        LocalDate startDate = windowStart.toLocalDate();
        LocalDate endDate = windowEnd.toLocalDate();

        if (startDate.equals(endDate)) {
            return queryTargets(startDate, windowStart.toLocalTime(), windowEnd.toLocalTime());
        }

        List<NudgeDoriTaskTarget> nudgeTargets = new ArrayList<>();
        nudgeTargets.addAll(queryTargets(startDate, windowStart.toLocalTime(), LocalTime.MAX));
        nudgeTargets.addAll(queryTargets(endDate, LocalTime.MIN, windowEnd.toLocalTime()));
        return nudgeTargets;
    }

    private List<NudgeDoriTaskTarget> queryTargets(LocalDate date, LocalTime startTimeFrom, LocalTime startTimeTo) {
        return jdbcTemplate.query(
                """
                        SELECT id         AS dowith_task_id,
                               title      AS dowith_task_title,
                               member_id  AS member_id
                            FROM dowith_task
                            WHERE status = ?
                                AND date = ?
                                AND start_time BETWEEN ? AND ?
                        """,
                (rs, rowNum) -> new NudgeDoriTaskTarget(
                        rs.getLong("dowith_task_id"), rs.getString("dowith_task_title"), rs.getString("member_id")),
                DowithTaskStatus.WAIT.code,
                date,
                startTimeFrom,
                startTimeTo);
    }

    public record NudgeDoriTaskTarget(Long dowithTaskId, String dowithTaskTitle, String memberId) {}
}
