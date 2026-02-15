package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.FeedbackAvailableDowithTasksQueryDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DowithTaskQueryRepository {

    List<DowithTaskQueryDto> getDowithTasks(String memberId, LocalDate startDate,
        LocalDate endDate);

    Optional<DowithTaskDetailQueryDto> getDowithTask(String memberId, Long dowithTaskId);

    List<FeedbackAvailableDowithTasksQueryDto> getFeedbackAvailableDowithTasks(Long offset,
        int size);
}