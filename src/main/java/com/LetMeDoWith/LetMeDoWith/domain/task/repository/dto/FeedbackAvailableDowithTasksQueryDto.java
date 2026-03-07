package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.SentFeedbacksQueryDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record FeedbackAvailableDowithTasksQueryDto(
        Long id,
        String memberId,
        String nickname,
        String badgeImageUrl,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        int feedbackCount,
        List<SentFeedbacksQueryDto> myFeedbacks) {

    public static FeedbackAvailableDowithTasksQueryDto of(
            FeedbackAvailableDowithTasksQueryDto task, Long count, List<SentFeedbacksQueryDto> myFeedbacks) {
        return new FeedbackAvailableDowithTasksQueryDto(
                task.id(),
                task.memberId(),
                task.nickname(),
                task.badgeImageUrl(),
                task.title(),
                task.status(),
                task.date(),
                task.startTime(),
                count.intValue(),
                myFeedbacks);
    }
}
