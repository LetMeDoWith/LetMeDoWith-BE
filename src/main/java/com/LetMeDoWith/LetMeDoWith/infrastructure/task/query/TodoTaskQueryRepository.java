package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;
import java.time.LocalDate;
import java.util.List;

public interface TodoTaskQueryRepository {

    List<TodoTaskQueryDto> getTodoTasks(String memberId, LocalDate startDate, LocalDate endDate);
    //    TodoTaskDetailQueryDto getTodoTask(String memberId, Long todoTaskId); // TODO - 선종 TodoTaskRoutine 도메인 모델 수정 후
    // 해당 부분 적용 필요
}
