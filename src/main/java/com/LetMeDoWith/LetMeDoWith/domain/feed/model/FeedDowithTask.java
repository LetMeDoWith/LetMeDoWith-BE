package com.LetMeDoWith.LetMeDoWith.domain.feed.model;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 둘러보기에서 두윗을 조회하기 위한 조회모델
 */
public record FeedDowithTask(
        Long id,
        String memberId,
        String nickname,
        String badgeImageUrl,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        Long feedbackCount) {

    public static FeedDowithTask of(
            Long id,
            String memberId,
            String nickname,
            String badgeImageUrl,
            String title,
            String status,
            LocalDate date,
            LocalTime startTime,
            Long feedbackCount) {
        return new FeedDowithTask(id, memberId, nickname, badgeImageUrl, title, status, date, startTime, feedbackCount);
    }

    public LocalDateTime startDateTime() {
        return LocalDateTime.of(date, startTime);
    }

    /**
     * 해당 두윗이 아직 잔소리 부여 가능 상태인지 판단.
     *
     * @return 조회 모델의 두윗이 잔소리 가능 한지 여부
     */
    public Boolean isFeedbackAvailable() {
        return startDateTime().plusHours(1).isAfter(SystemTimeUtil.now());
    }
}
