package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

import java.time.LocalDateTime;

public record FailedDowithTaskCountQueryDto(
        String memberId,
        Long failedTaskCount,
        LocalDateTime latestFailedTaskStartDateTime,
        LocalDateTime earliestFailedTaskCreatedDateTime) {}
