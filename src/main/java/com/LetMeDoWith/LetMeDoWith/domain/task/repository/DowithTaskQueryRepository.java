package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.SuccessDowithTaskQueryDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DowithTaskQueryRepository {

    List<DowithTaskQueryDto> getDowithTasks(String memberId, LocalDate startDate, LocalDate endDate);

    Optional<DowithTaskDetailQueryDto> getDowithTask(String memberId, Long dowithTaskId);

    List<SuccessDowithTaskQueryDto> getSuccessDowithTasks(String requestMemberId, int offset, int limit);

    Map<Long, Long> countDowithTaskLikes(Set<Long> dowithTaskIds);
}
