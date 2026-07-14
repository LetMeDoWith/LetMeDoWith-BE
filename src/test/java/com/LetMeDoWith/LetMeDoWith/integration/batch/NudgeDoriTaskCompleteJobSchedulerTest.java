package com.LetMeDoWith.LetMeDoWith.integration.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.batch.scheduler.DowithTaskJobScheduler;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTokenJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NudgeDoriTaskCompleteJobSchedulerTest extends AbstractIntegrationTest {

    // TODO - 테스트 FCM 토큰 generator에서 발급 받은 토큰 세팅
    private final String REGISTERED_FCM_TOKEN =
            "fx5STrP_eh7XIRNiVvNBk_:APA91bHpJ_SvZQTs8SK-Hkl5d8vChDEb2_njBRp-uLtzWU-3_s5W9aoL6OprShJG-ZIU4oSSDD4cfvB0jKb8xUcjvLWyVvhDkiM9DhsdrxhKa0wwrDwx-YI";

    @Autowired
    private DowithTaskJobScheduler dowithTaskJobScheduler;

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    private NotificationJpaRepository notificationJpaRepository;

    @Autowired
    private NotificationTemplateJpaRepository notificationTemplateJpaRepository;

    @Autowired
    private NotificationTokenJpaRepository notificationTokenJpaRepository;

    private DowithTask target10mTask;
    private DowithTask target30mTask;
    private DowithTask target50mTask;
    private DowithTask nonTargetTask;

    private NotificationToken notificationToken;

    private NotificationTemplate template10m;
    private NotificationTemplate template30m;
    private NotificationTemplate template50m;

    @Override
    protected void deleteTestData() {
        notificationJpaRepository.findAll().stream()
                .filter(notification -> notification.getMemberId().equals(requestMember.getId()))
                .forEach(notificationJpaRepository::delete);
        notificationTokenJpaRepository.delete(notificationToken);
        notificationTemplateJpaRepository.delete(template10m);
        notificationTemplateJpaRepository.delete(template30m);
        notificationTemplateJpaRepository.delete(template50m);
        dowithTaskJpaRepository.deleteAll(List.of(target10mTask, target30mTask, target50mTask, nonTargetTask));
    }

    @Override
    protected void createTestData() {
        // DowithTask 생성 시점 검증(오늘 이전 날짜 불가) 회피를 위해 대상 날짜보다 이전 시각으로 고정
        this.setFixedClock(LocalDateTime.of(2025, 2, 1, 0, 0));

        // 실행 시각 2025-03-02 00:15 기준 10분 전 = 2025-03-02 00:05 (같은 날)
        target10mTask = dowithTaskJpaRepository.save(
                DowithTask.of(requestMember.getId(), null, "10분 경과 테스트", LocalDate.of(2025, 3, 2), LocalTime.of(0, 5)));

        // 실행 시각 기준 30분 전 = 2025-03-01 23:45 (자정을 넘어 전날로 이동하는 케이스)
        target30mTask = dowithTaskJpaRepository.save(DowithTask.of(
                requestMember.getId(), null, "30분 경과 테스트", LocalDate.of(2025, 3, 1), LocalTime.of(23, 45)));

        // 실행 시각 기준 50분 전 = 2025-03-01 23:25 (자정을 넘어 전날로 이동하는 케이스)
        target50mTask = dowithTaskJpaRepository.save(DowithTask.of(
                requestMember.getId(), null, "50분 경과 테스트", LocalDate.of(2025, 3, 1), LocalTime.of(23, 25)));

        // 어느 버킷(10/30/50분)에도 해당하지 않는 태스크 - 알림이 발송되지 않아야 함
        nonTargetTask = dowithTaskJpaRepository.save(DowithTask.of(
                requestMember.getId(), null, "매칭되지 않는 태스크", LocalDate.of(2025, 3, 1), LocalTime.of(23, 50)));

        notificationToken =
                notificationTokenJpaRepository.save(NotificationToken.of(requestMember.getId(), REGISTERED_FCM_TOKEN));

        // sendNotifications는 title/body를 전체 수신자에 공통 적용하므로, 개인화(nickname 등) 없이 고정 문구만 사용한다.
        // deeplink만 수신자(=태스크) 별로 dowithTaskId가 다르게 채워진다.
        template10m = notificationTemplateJpaRepository.save(NotificationTemplate.of(
                NotificationTemplateCode.NUDGE_DORI_COMPLETE_10M,
                NotificationType.NORMAL,
                "10분 전 시작한 도리",
                "아직 완료하지 못한 도리가 있어요!",
                "letmedowith://task/{{dowithTaskId}}"));
        template30m = notificationTemplateJpaRepository.save(NotificationTemplate.of(
                NotificationTemplateCode.NUDGE_DORI_COMPLETE_30M,
                NotificationType.NORMAL,
                "30분 전 시작한 도리",
                "아직 완료하지 못한 도리가 있어요!",
                "letmedowith://task/{{dowithTaskId}}"));
        template50m = notificationTemplateJpaRepository.save(NotificationTemplate.of(
                NotificationTemplateCode.NUDGE_DORI_COMPLETE_50M,
                NotificationType.NORMAL,
                "50분 전 시작한 도리",
                "아직 완료하지 못한 도리가 있어요!",
                "letmedowith://task/{{dowithTaskId}}"));
    }

    @Test
    void testNudgeDoriTaskCompleteJob() {
        // given
        this.setFixedClock(LocalDateTime.of(2025, 3, 2, 0, 15));

        // when
        dowithTaskJobScheduler.runNudgeDoriTaskCompleteJob();

        // then
        List<Notification> notifications = notificationJpaRepository.findAll().stream()
                .filter(notification -> notification.getMemberId().equals(requestMember.getId()))
                .toList();

        assertThat(notifications).hasSize(3);
        assertThat(notifications.stream().map(Notification::getNotificationTemplateCode))
                .containsExactlyInAnyOrder(
                        NotificationTemplateCode.NUDGE_DORI_COMPLETE_10M.getCode(),
                        NotificationTemplateCode.NUDGE_DORI_COMPLETE_30M.getCode(),
                        NotificationTemplateCode.NUDGE_DORI_COMPLETE_50M.getCode());

        // 버킷 별 알림의 deeplink에 해당 버킷의 dowithTaskId가 정확히 들어갔는지 확인
        Map<String, Long> expectedDowithTaskIdByTemplateCode = Map.of(
                NotificationTemplateCode.NUDGE_DORI_COMPLETE_10M.getCode(), target10mTask.getId(),
                NotificationTemplateCode.NUDGE_DORI_COMPLETE_30M.getCode(), target30mTask.getId(),
                NotificationTemplateCode.NUDGE_DORI_COMPLETE_50M.getCode(), target50mTask.getId());

        for (Notification notification : notifications) {
            Long expectedDowithTaskId =
                    expectedDowithTaskIdByTemplateCode.get(notification.getNotificationTemplateCode());
            assertThat(notification.getDeepLink()).isEqualTo("letmedowith://task/" + expectedDowithTaskId);
        }
    }
}
