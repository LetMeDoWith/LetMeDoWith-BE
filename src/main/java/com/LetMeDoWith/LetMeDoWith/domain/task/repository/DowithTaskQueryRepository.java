package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskQueryDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DowithTaskQueryRepository {

    List<DowithTaskQueryDto> getDowithTasks(String memberId, LocalDate startDate, LocalDate endDate);

    Optional<DowithTaskDetailQueryDto> getDowithTask(String memberId, Long dowithTaskId);
}
