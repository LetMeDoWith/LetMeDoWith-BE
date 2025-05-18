package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;

import java.time.LocalDate;
import java.util.List;

public interface TodoTaskQueryRepository {

    List<TodoTaskQueryDto> getTodoTasks(String memberId, LocalDate startDate, LocalDate endDate);
}
