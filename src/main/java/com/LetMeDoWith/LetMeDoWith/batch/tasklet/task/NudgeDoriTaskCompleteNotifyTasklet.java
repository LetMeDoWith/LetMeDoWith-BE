package com.LetMeDoWith.LetMeDoWith.batch.tasklet.task;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.SendNotificationResult;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationSendService;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class NudgeDoriTaskCompleteNotifyTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    private final NotificationSendService notificationSendService;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        for (DelayedMinutes delayedMinutes : DelayedMinutes.values()) {
            nudge(delayedMinutes);
        }

        return RepeatStatus.FINISHED;
    }

    private void nudge(DelayedMinutes delayedMinutes) {

        // start_time이 DelayedMinutes지난 DowithTask들의 dotiwhTaskId, dowithTaskTitle, member_id, nickname 조회하기
        // 시작 시각이 (실행시각 - DelayedMinutes)인 태스크가 대상. 배치가 분 단위로 실행되므로 해당 '분'(00~59초) 윈도우로 매칭.
        // executionDateTime에서 분을 빼기 때문에 자정을 넘어가는 경우(예: 00:15 실행 - 30분 = 전날 23:45)도 date/time이 함께 이동해 자동으로 처리됨.
        LocalDateTime standardDateTime = executionDateTime.minusMinutes(delayedMinutes.getMinutes());
        LocalDate standardDate = standardDateTime.toLocalDate();
        LocalTime startTimeFrom = standardDateTime.toLocalTime().withSecond(0).withNano(0);
        LocalTime startTimeTo = startTimeFrom.plusSeconds(59);

        List<NudgeDoriTaskTarget> nudgeTargets = this.jdbcTemplate.query(
                """
                        SELECT dt.id         AS dowith_task_id,
                               dt.title      AS dowith_task_title,
                               dt.member_id  AS member_id,
                               m.nickname    AS nickname
                            FROM dowith_task dt
                            JOIN member m ON m.id = dt.member_id
                            WHERE dt.status = ?
                                AND dt.date = ?
                                AND dt.start_time BETWEEN ? AND ?
                        """,
                (rs, rowNum) -> new NudgeDoriTaskTarget(
                        rs.getLong("dowith_task_id"),
                        rs.getString("dowith_task_title"),
                        rs.getString("member_id"),
                        rs.getString("nickname")),
                DowithTaskStatus.WAIT.code,
                standardDate,
                startTimeFrom,
                startTimeTo);

        if (nudgeTargets.isEmpty()) {
            return;
        }

        List<String> receiverMemberIds =
                nudgeTargets.stream().map(NudgeDoriTaskTarget::memberId).toList();
        List<Map<String, String>> deeplinkParams = nudgeTargets.stream()
                .map(target -> Map.of("dowithTaskId", String.valueOf(target.dowithTaskId())))
                .toList();

        SendNotificationResult sendNotificationResult = notificationSendService.sendNotifications(
                delayedMinutes.getNotificationTemplateCode(), receiverMemberIds, null, null, deeplinkParams);

        if (!sendNotificationResult.failedMemberIds().isEmpty()) {
            for (NudgeDoriTaskTarget target : nudgeTargets) {
                if (sendNotificationResult.failedMemberIds().contains(target.memberId())) {
                    log.error(
                            "도리 완료 재촉 알림({}M) 발송 실패 - memberId: {}, dowithTaskId: {}",
                            delayedMinutes.minutes,
                            target.memberId(),
                            target.dowithTaskId());
                }
            }
        }
    }

    public enum DelayedMinutes {
        MIN_10(10, NotificationTemplateCode.NUDGE_DORI_COMPLETE_10M),
        MIN_30(30, NotificationTemplateCode.NUDGE_DORI_COMPLETE_30M),
        MIN_50(50, NotificationTemplateCode.NUDGE_DORI_COMPLETE_50M);

        private final int minutes;
        private final NotificationTemplateCode notificationTemplateCode;

        DelayedMinutes(int minutes, NotificationTemplateCode notificationTemplateCode) {
            this.minutes = minutes;
            this.notificationTemplateCode = notificationTemplateCode;
        }

        public int getMinutes() {
            return minutes;
        }

        public NotificationTemplateCode getNotificationTemplateCode() {
            return notificationTemplateCode;
        }
    }

    public record NudgeDoriTaskTarget(Long dowithTaskId, String dowithTaskTitle, String memberId, String nickname) {}
}
