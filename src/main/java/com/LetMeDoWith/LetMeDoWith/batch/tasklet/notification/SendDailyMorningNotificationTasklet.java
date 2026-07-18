package com.LetMeDoWith.LetMeDoWith.batch.tasklet.notification;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.SendNotificationResult;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationSendService;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class SendDailyMorningNotificationTasklet implements Tasklet {

    private final MemberJpaRepository memberJpaRepository;
    private final NotificationSendService notificationSendService;

    @Value("#{jobParameters['executionDateTime']}")
    private LocalDateTime executionDateTime;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<Member> normalMembers = memberJpaRepository.findAllByStatusIn(List.of(MemberStatus.NORMAL));
        if (normalMembers.isEmpty()) {
            return RepeatStatus.FINISHED;
        }

        NotificationTemplateCode templateCode = resolveTemplateCode(executionDateTime.getDayOfWeek());
        List<String> receiverMemberIds =
                normalMembers.stream().map(Member::getId).toList();

        SendNotificationResult sendNotificationResult =
                notificationSendService.sendNotifications(templateCode, receiverMemberIds, null, null, null);

        if (!sendNotificationResult.failedMemberIds().isEmpty()) {
            log.error(
                    "데일리 아침 알림 발송 실패 - templateCode: {}, failedMemberIds: {}",
                    templateCode,
                    sendNotificationResult.failedMemberIds());
        }

        return RepeatStatus.FINISHED;
    }

    NotificationTemplateCode resolveTemplateCode(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> NotificationTemplateCode.DAILY_MON_AM;
            case TUESDAY -> NotificationTemplateCode.DAILY_TUES_AM;
            case WEDNESDAY -> NotificationTemplateCode.DAILY_WED_AM;
            case THURSDAY -> NotificationTemplateCode.DAILY_THURS_AM;
            case FRIDAY -> NotificationTemplateCode.DAILY_FRI_AM;
            case SATURDAY -> NotificationTemplateCode.DAILY_SAT_AM;
            case SUNDAY -> NotificationTemplateCode.DAILY_SUN_AM;
        };
    }
}
