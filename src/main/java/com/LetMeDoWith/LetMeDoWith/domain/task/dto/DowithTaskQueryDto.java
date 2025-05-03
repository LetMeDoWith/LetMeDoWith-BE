package com.LetMeDoWith.LetMeDoWith.domain.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record DowithTaskQueryDto(
    Long id, // TODO - 추후 PK 정책에 따른 수정 필요
    Long taskCategoryId,
    String taskCategoryName,
    String title,
    DowithTaskStatus status,
    LocalDate date,
    LocalTime startTime,
    String confirmedImageUrl,
    int feedBackCount
) {

}
