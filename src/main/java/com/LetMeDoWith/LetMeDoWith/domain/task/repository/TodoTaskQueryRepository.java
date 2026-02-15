package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.TodoTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.TodoTaskQueryDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoTaskQueryRepository {

    List<TodoTaskQueryDto> getTodoTasks(String memberId, LocalDate startDate, LocalDate endDate);

    Optional<TodoTaskDetailQueryDto> getTodoTask(String memberId, Long todoTaskId);
}
