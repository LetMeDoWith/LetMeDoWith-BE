package com.LetMeDoWith.LetMeDoWith.batch.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
public class DowithTaskDto {
    private Long id;
    private String memberId;
    private Long taskCategoryId;
    private String title;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalDateTime successAt;
    private LocalDateTime completeAt;
}
