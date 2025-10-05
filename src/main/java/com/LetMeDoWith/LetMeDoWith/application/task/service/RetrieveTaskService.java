package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveDowithTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.TodoTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrieveTaskService {

    private final TodoTaskQueryRepository todoTaskQueryRepository;
    private final DowithTaskQueryRepository dowithTaskQueryRepository;

    /**
     * DowithTask 조회
     *
     * @param dowithTaskId
     * @return
     */
    @Transactional(readOnly = true)
    public RetrieveDowithTaskResult retrieveDowithTask(Long dowithTaskId) {
        String memberId = AuthUtil.getMemberId();
        DowithTaskDetailQueryDto result = dowithTaskQueryRepository
                .getDowithTask(memberId, dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        return RetrieveDowithTaskResult.from(result);
    }

    /**
     * TodoTask 조회
     *
     * @param todoTaskId
     */
    @Transactional(readOnly = true)
    public RetrieveTodoTaskResult retrieveTodoTask(Long todoTaskId) {
        String memberId = AuthUtil.getMemberId();

        TodoTaskDetailQueryDto result = todoTaskQueryRepository
                .getTodoTask(memberId, todoTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        return RetrieveTodoTaskResult.from(result);
    }

    @Transactional(readOnly = true)
    public RetrieveTasksResult retrieveMonthTasks(Year year, Month month) {
        String memberId = AuthUtil.getMemberId();
        LocalDate startDate = LocalDate.of(year.getValue(), month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // TODO - 추후 Cache 적용

        List<TodoTaskQueryDto> todoTaskQueryDtos = todoTaskQueryRepository.getTodoTasks(memberId, startDate, endDate);
        List<DowithTaskQueryDto> dowithTaskQueryDtos =
                dowithTaskQueryRepository.getDowithTasks(memberId, startDate, endDate);

        return RetrieveTasksResult.of(todoTaskQueryDtos, dowithTaskQueryDtos);
    }
}
