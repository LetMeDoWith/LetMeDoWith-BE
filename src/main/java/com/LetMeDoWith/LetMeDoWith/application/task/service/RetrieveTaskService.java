package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTasksResult;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.TodoTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrieveTaskService {

    private final TodoTaskQueryRepository todoTaskQueryRepository;
    private final DowithTaskQueryRepository dowithTaskQueryRepository;

    @Transactional(readOnly = true)
    public RetrieveTasksResult retrieveMonthTasks(String memberId, Year year, Month month) {
        LocalDate startDate = LocalDate.of(year.getValue(), month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // TODO - 추후 Cache 적용

        List<TodoTaskQueryDto> todoTasks =
                todoTaskQueryRepository.getTodoTasks(memberId, startDate, endDate);
        List<DowithTaskQueryDto> dowithTasks =
                dowithTaskQueryRepository.getDowithTasks(memberId, startDate, endDate);

        return RetrieveTasksResult.of(todoTasks, dowithTasks);
    }
}
