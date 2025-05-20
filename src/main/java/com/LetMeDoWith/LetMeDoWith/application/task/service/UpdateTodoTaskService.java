package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineDateCalculator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTodoTaskService {
    
    private final TodoTaskRepository todoTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    
    private final TodoTaskRoutineDateCalculator routineDateCalculator;
    
    private final HolidayService holidayService;
    
    /**
     * 루틴이 아닌 TodoTask를 업데이트한다.
     * 컨텐츠를 업데이트 하거나, 루틴으로 변경할 수도 있다.
     *
     * @param memberId   TodoTask를 업데이트할 사용자의 ID
     * @param todoTaskId 업데이트할 TodoTask의 ID
     * @param command    업데이트할 정보 (카테고리 ID, 제목, 시작일, 시작시간, 루틴정보)
     */
    public void updateSingleTodoTask(String memberId,
                                     Long todoTaskId,
                                     UpdateTodoTaskCommand command) {
        
        TodoTask todoTask = todoTaskRepository
            .getTodoTask(todoTaskId, memberId)
            .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        
        TaskCategory category = taskCategoryRepository.
            getActiveTaskCategory(command.taskCategoryId(), memberId)
            .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        
        if (Boolean.FALSE.equals(todoTask.isContentsEditable())) {
            throw new RestApiException(INVALID_REQUEST);
        }
        
        // 우선 컨텐츠를 업데이트
        todoTask.updateContent(command.title(),
                               category.getId(),
                               todoTask.getDate(),
                               command.startTime());
        
        if (command.routineCondition().isPresent()) {
            // 루틴 정보가 있다면 루틴 변환이므로 변환해주고 저장한다.
            TodoTaskRoutineCondition routineCondition = command.routineCondition()
                                                               .orElseThrow(() -> new RestApiException(
                                                                   FailResponseStatus.INVALID_REQUEST));
            
            // 루틴 반복 주기에 따른 루틴 수행일자 계산
            Set<LocalDate> routineDates =
                routineDateCalculator.computeRoutineDates(
                    routineCondition.cycle(),
                    routineCondition.startDate(),
                    routineCondition.endDate(),
                    routineCondition.pattern());
            
            if (Boolean.TRUE.equals(routineCondition.isExcludeHolidays())) {
                // 공휴일 목록 조회
                Set<LocalDate> holidays =
                    holidayService.getHolidays(CountryCode.KR,
                                               routineCondition.startDate(),
                                               routineCondition.endDate());
                
                routineDates.removeAll(holidays);
            }
            
            List<TodoTask> todoTasksWithRoutine =
                todoTask.createRoutine(routineDates,
                                       routineCondition.cycle(),
                                       routineCondition.pattern(),
                                       routineCondition.isExcludeHolidays());
            
            todoTaskRepository.saveTodoTasks(todoTasksWithRoutine);
        }
    }
}