package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskWithRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.HolidayRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskSummaryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TaskRoutineDateCalculator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDowithTaskService {

    private final DowithTaskRepository dowithTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TaskSummaryRepository taskSummaryRepository;

    private final HolidayRepository holidayRepository;

    private final TaskRoutineDateCalculator taskRoutineDateCalculator;

    /**
     * 두윗모드 Task 생성
     *
     * @param command
     */
    @Transactional
    public DowithTask createDowithTask(CreateDowithTaskCommand command) {

        String memberId = AuthUtil.getMemberId();
        if (command.taskCategoryId() != null) {
            taskCategoryRepository
                    .getActiveTaskCategory(command.taskCategoryId(), memberId)
                    .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        }

        // 두윗모드 Task 사용 개수 제한 정책 무효로 주석 처리
        //        TaskSummary taskSummary = taskSummaryRepository
        //                .getTaskSummary(memberId)
        //                .orElseThrow(() -> new RestApiException(INTERNAL_SERVER_ERROR));
        //        taskSummary.deductRemainedDowithTaskCount(targetDateSet.size());

        DowithTask dowithTask =
                DowithTask.of(memberId, command.taskCategoryId(), command.title(), command.date(), command.startTime());

        return dowithTaskRepository.saveDowithTask(dowithTask);
    }

    /**
     * 두윗모드 Task 생성 - 루틴이 있는 경우
     *
     * @param command
     * @return
     */
    @Transactional
    public List<DowithTask> createDowithTaskWithRoutine(CreateDowithTaskWithRoutineCommand command) {

        String memberId = AuthUtil.getMemberId();
        List<DowithTask> dowithTasks = new ArrayList<>();
        if (command.taskCategoryId() != null) {
            taskCategoryRepository
                    .getActiveTaskCategory(command.taskCategoryId(), memberId)
                    .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        }

        // 두윗모드 Task 사용 개수 제한 정책 무효로 주석 처리
        //        TaskSummary taskSummary = taskSummaryRepository
        //                .getTaskSummary(memberId)
        //                .orElseThrow(() -> new RestApiException(INTERNAL_SERVER_ERROR));
        //        taskSummary.deductRemainedDowithTaskCount(targetDateSet.size());

        DowithTask dowithTask = DowithTask.of(
                memberId,
                command.taskCategoryId(),
                command.title(),
                command.date(),
                command.startTime(),
                command.routineCondition().startDate(),
                command.routineCondition().endDate(),
                command.routineCondition().cycle(),
                command.routineCondition().pattern(),
                command.routineCondition().isExcludeHolidays());
        dowithTasks.add(dowithTask);

        // 루틴 반복 조건에 따른 루틴 일자 계산
        Set<Holiday> holidaySet = Set.of();
        if (command.routineCondition().isExcludeHolidays()) {
            holidaySet = holidayRepository.getHolidays(
                    CountryCode.KR,
                    command.routineCondition().startDate(),
                    command.routineCondition().endDate());
        }
        Set<LocalDate> routineDates = this.taskRoutineDateCalculator.calculateRoutineDates(dowithTask, holidaySet);
        dowithTasks.addAll(DowithTask.of(dowithTask, routineDates));

        return dowithTaskRepository.saveDowithTasks(dowithTasks);
    }
}
