package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import java.time.LocalDate;
import java.util.List;

public interface DowithTaskQueryRepository {

    List<DowithTaskQueryDto> getDowithTasks(Long memberId, LocalDate startDate, LocalDate endDate);
}
