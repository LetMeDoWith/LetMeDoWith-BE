package com.LetMeDoWith.LetMeDoWith.batch.tasklet.notification.task;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.SendNotificationResult;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationSendService;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
public class FailDoriTaskNotifyTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    private final NotificationSendService notificationSendService;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        // 시작한지 1시간이 지난 WAIT 상태의 DowithTask들 조회 (UpdateFailDowithTaskTasklet과 동일한 기준)
        // 이 Step은 스케줄러에서 UpdateFailDowithTaskTasklet(상태를 FAIL로 변경)보다 반드시 먼저 실행되어야
        // status = WAIT 조건으로 대상을 정확히 한 번만 찾아낼 수 있다.
        LocalDateTime standardDateTime = executionDateTime.minusHours(1);

        List<FailDoriTaskTarget> failTargets = this.jdbcTemplate.query(
                """
                        SELECT id         AS dowith_task_id,
                               title      AS dowith_task_title,
                               member_id  AS member_id
                            FROM dowith_task
                            WHERE status = ?
                                AND TIMESTAMP(date, start_time) <= ?
                        """,
                (rs, rowNum) -> new FailDoriTaskTarget(
                        rs.getLong("dowith_task_id"), rs.getString("dowith_task_title"), rs.getString("member_id")),
                DowithTaskStatus.WAIT.code,
                Timestamp.valueOf(standardDateTime));

        if (failTargets.isEmpty()) {
            return RepeatStatus.FINISHED;
        }

        List<String> receiverMemberIds =
                failTargets.stream().map(FailDoriTaskTarget::memberId).toList();
        List<Map<String, String>> bodyParams = failTargets.stream()
                .map(target -> Map.of("doriTaskTitle", target.dowithTaskTitle()))
                .toList();

        SendNotificationResult sendNotificationResult = notificationSendService.sendNotifications(
                NotificationTemplateCode.DORI_FAIL, receiverMemberIds, null, bodyParams, null);

        if (!sendNotificationResult.failedMemberIds().isEmpty()) {
            for (FailDoriTaskTarget target : failTargets) {
                if (sendNotificationResult.failedMemberIds().contains(target.memberId())) {
                    log.error(
                            "도리 Todo 실패 알림 발송 실패 - memberId: {}, dowithTaskId: {}",
                            target.memberId(),
                            target.dowithTaskId());
                }
            }
        }

        return RepeatStatus.FINISHED;
    }

    public record FailDoriTaskTarget(Long dowithTaskId, String dowithTaskTitle, String memberId) {}
}
