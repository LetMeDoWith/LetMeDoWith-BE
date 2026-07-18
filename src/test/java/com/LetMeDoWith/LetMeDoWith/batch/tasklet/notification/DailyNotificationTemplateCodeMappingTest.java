package com.LetMeDoWith.LetMeDoWith.batch.tasklet.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import java.time.DayOfWeek;
import org.junit.jupiter.api.Test;

class DailyNotificationTemplateCodeMappingTest {

    private final SendDailyMorningNotificationTasklet morningTasklet =
            new SendDailyMorningNotificationTasklet(null, null);
    private final SendDailyEveningNotificationTasklet eveningTasklet =
            new SendDailyEveningNotificationTasklet(null, null);

    @Test
    void resolveTemplateCode_아침_배치는_요일별로_DAILY_AM_코드를_반환한다() {
        assertThat(morningTasklet.resolveTemplateCode(DayOfWeek.MONDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_MON_AM);
        assertThat(morningTasklet.resolveTemplateCode(DayOfWeek.TUESDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_TUES_AM);
        assertThat(morningTasklet.resolveTemplateCode(DayOfWeek.WEDNESDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_WED_AM);
        assertThat(morningTasklet.resolveTemplateCode(DayOfWeek.THURSDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_THURS_AM);
        assertThat(morningTasklet.resolveTemplateCode(DayOfWeek.FRIDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_FRI_AM);
        assertThat(morningTasklet.resolveTemplateCode(DayOfWeek.SATURDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_SAT_AM);
        assertThat(morningTasklet.resolveTemplateCode(DayOfWeek.SUNDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_SUN_AM);
    }

    @Test
    void resolveTemplateCode_저녁_배치는_요일별로_DAILY_PM_코드를_반환한다() {
        assertThat(eveningTasklet.resolveTemplateCode(DayOfWeek.MONDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_MON_PM);
        assertThat(eveningTasklet.resolveTemplateCode(DayOfWeek.TUESDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_TUES_PM);
        assertThat(eveningTasklet.resolveTemplateCode(DayOfWeek.WEDNESDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_WED_PM);
        assertThat(eveningTasklet.resolveTemplateCode(DayOfWeek.THURSDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_THURS_PM);
        assertThat(eveningTasklet.resolveTemplateCode(DayOfWeek.FRIDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_FRI_PM);
        assertThat(eveningTasklet.resolveTemplateCode(DayOfWeek.SATURDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_SAT_PM);
        assertThat(eveningTasklet.resolveTemplateCode(DayOfWeek.SUNDAY))
                .isEqualTo(NotificationTemplateCode.DAILY_SUN_PM);
    }
}
