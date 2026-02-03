package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetreiveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveDowithTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.DowithTaskFeedbackQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.SentFeedbacksQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.TodoTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.FeedbackAvailableDowithTasksQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetrieveTaskService {

    private final TodoTaskQueryRepository todoTaskQueryRepository;
    private final DowithTaskQueryRepository dowithTaskQueryRepository;
    private final DowithTaskFeedbackQueryRepository dowithTaskFeedbackQueryRepository;

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

    /**
     * 잔소리 가능한 두윗 및 내가 보낸 잔소리 목록 조회
     *
     * @param pageable 페이지네이션 객체
     * @return 잔소리 가능 두윗 목록과 내가 보낸 잔소리
     */
    @Transactional(readOnly = true)
    public RetreiveFeedbackAvailableDowithTasksResult retrieveFeedbackAvailableDowithTasks(Pageable pageable) {
        String memberId = AuthUtil.getMemberId();

        // 1. Task 리스트 조회
        List<FeedbackAvailableDowithTasksQueryDto> feedbackAvailableDowithTasks =
                dowithTaskQueryRepository.getFeedbackAvailableDowithTasks(pageable.getOffset(), pageable.getPageSize());

        if (feedbackAvailableDowithTasks.isEmpty()) {
            return RetreiveFeedbackAvailableDowithTasksResult.from(feedbackAvailableDowithTasks);
        }

        List<Long> taskIds = feedbackAvailableDowithTasks.stream()
                .map(FeedbackAvailableDowithTasksQueryDto::id)
                .toList();

        // 2. Feedback Count 조회
        Map<Long, Long> feedbackCountMap = dowithTaskFeedbackQueryRepository.countFeedbacksByTaskIds(taskIds);

        // 3. 내가 보낸 Feedback 조회
        Map<Long, List<SentFeedbacksQueryDto>> myFeedbacksMap =
                dowithTaskFeedbackQueryRepository.getSentFeedbacks(memberId, taskIds);

        // 4. 데이터 조합
        List<FeedbackAvailableDowithTasksQueryDto> tasks = feedbackAvailableDowithTasks.stream()
                .map(task -> {
                    Long count = feedbackCountMap.getOrDefault(task.id(), 0L);
                    List<SentFeedbacksQueryDto> myFeedbacks =
                            myFeedbacksMap.getOrDefault(task.id(), Collections.emptyList());

                    return FeedbackAvailableDowithTasksQueryDto.of(task, count, myFeedbacks);
                })
                .toList();

        return RetreiveFeedbackAvailableDowithTasksResult.from(tasks);
    }
}
