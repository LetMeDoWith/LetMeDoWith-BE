package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.TodoTaskQueryDto;
import java.time.LocalDate;
import java.util.List;

public interface TaskQueryRepository {
    
    List<TodoTaskQueryDto> getTodoTasks(Long memberId, LocalDate startDate, LocalDate endDate);
    
    List<DowithTaskQueryDto> getDowithTasks(Long memberId, LocalDate startDate, LocalDate endDate);
    
}
