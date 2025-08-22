package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_PARAM_ERROR;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskContentsAndCreateRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskContentsOnlyCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.*;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TaskRoutineDateCalculator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDowithTaskService {

    private final TaskRoutineDateCalculator taskRoutineDateCalculator;

    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskRoutineRepository dowithTaskRoutineRepository;
    private final TaskSummaryRepository taskSummaryRepository;

    private final TaskCategoryRepository taskCategoryRepository;

    private final HolidayRepository holidayRepository;

    /**
     * 두윗모드Task 내용 수정 및 루틴 생성
     *
     * @param command
     */
    @Transactional
    public void updateDowithTaskContentsAndCreateRoutine(UpdateDowithTaskContentsAndCreateRoutineCommand command) {

        String memberId = AuthUtil.getMemberId();
        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(command.dowithTaskId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (!command.taskRoutineCondition().startDate().isEqual(dowithTask.getDate()))
            throw new RestApiException(INVALID_PARAM_ERROR);

        if (command.taskCategoryId() != null)
            taskCategoryRepository
                    .getActiveTaskCategory(command.taskCategoryId(), memberId)
                    .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (dowithTask.isStarted()) {
            dowithTask.updateContents(command.title(), command.taskCategoryId(), command.date(), command.startTime());
        } else {
            dowithTask.updateContents(command.title(), command.taskCategoryId());
        }

        Set<Holiday> holidaySet = new HashSet<>();
        if (command.taskRoutineCondition().isExcludeHolidays()) {
            holidaySet = holidayRepository.getHolidays(
                    CountryCode.KR,
                    command.taskRoutineCondition().startDate(),
                    command.taskRoutineCondition().endDate());
        }

        TaskRoutineCondition taskRoutineCondition = command.taskRoutineCondition();
        dowithTask.createRoutine(
                taskRoutineCondition.startDate(),
                taskRoutineCondition.endDate(),
                taskRoutineCondition.cycle(),
                taskRoutineCondition.pattern(),
                taskRoutineCondition.isExcludeHolidays());
        Set<LocalDate> routineDates = taskRoutineDateCalculator.calculateRoutineDates(dowithTask, holidaySet);

        dowithTaskRepository.saveDowithTask(dowithTask);
        dowithTaskRepository.saveDowithTasks(DowithTask.of(dowithTask, routineDates));
    }

    /**
     * 두윗모드Task 내용만 수정
     *
     * @param command
     * @return
     */
    @Transactional
    public void updateDowithTaskContentsOnly(UpdateDowithTaskContentsOnlyCommand command) {

        String memberId = AuthUtil.getMemberId();
        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(command.dowithTaskId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (command.taskCategoryId() != null)
            taskCategoryRepository
                    .getActiveTaskCategory(command.taskCategoryId(), memberId)
                    .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (dowithTask.isStarted()) {
            // 시작 일시가 지난 Task인 경우
            dowithTask.updateContents(command.title(), command.taskCategoryId());

            if (dowithTask.isRoutine()) dowithTask.deleteRoutine();

        } else {
            // 시작 일시가 아직 지나지 않은 Task인 경우
            dowithTask.updateContents(command.title(), command.taskCategoryId(), command.date(), command.startTime());

            if (dowithTask.isRoutine()) {

                Set<Holiday> holidaySet = new HashSet<>();
                if (dowithTask.isRoutineExcludeHolidays()) {
                    holidaySet = holidayRepository.getHolidays(
                            CountryCode.KR,
                            dowithTask.getRoutine().getRangeStartDate(),
                            dowithTask.getRoutine().getRangeEndDate());
                }

                Set<LocalDate> toUpdateRoutineDates =
                        taskRoutineDateCalculator.calculateEditableRoutineDates(dowithTask, holidaySet);

                Map<Boolean, List<DowithTask>> partition =
                        dowithTaskRepository.getDowithTasks(dowithTask.getRoutine()).stream()
                                .collect(Collectors.partitioningBy(
                                        task -> toUpdateRoutineDates.contains(task.getDate())));
                partition.get(Boolean.FALSE).forEach(DowithTask::deleteRoutine);
                partition
                        .get(Boolean.TRUE)
                        .forEach(task -> task.updateContents(
                                command.title(), command.taskCategoryId(), command.date(), command.startTime()));
            }
        }
    }

    /**
     * 두윗모드Task 루틴 수정
     *
     * @param command
     * @return
     */
    @Transactional
    public DowithTask updateRoutine(UpdateDowithTaskRoutineCommand command) {

        String memberId = AuthUtil.getMemberId();
        final DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(command.dowithTaskId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (!dowithTask.getDate().isEqual(command.taskRoutineCondition().startDate()))
            throw new RestApiException(INVALID_PARAM_ERROR);

        if (!dowithTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        List<DowithTask> dowithTasks = dowithTaskRepository.getDowithTasks(dowithTask.getRoutine());

        Set<Holiday> holidaySet = new HashSet<>();
        if (dowithTask.isRoutineExcludeHolidays()) {
            holidaySet = holidayRepository.getHolidays(
                    CountryCode.KR,
                    DateTimeUtil.earlier(
                            dowithTask.getRoutine().getRangeStartDate(),
                            command.taskRoutineCondition().startDate()),
                    DateTimeUtil.earlier(
                            dowithTask.getRoutine().getRangeEndDate(),
                            command.taskRoutineCondition().endDate()));
        }

        // 수정 대상 루틴 날짜 계산
        // - 기존 루틴 일자에는 없고 새로운 루틴 일자에는 있어서 생성이 필요한 일자
        // - 기존 루틴 일자에는 있고 새로운 루틴 일자에는 없어서 삭제가 필요한 일자
        TaskRoutineDateCalculator.RoutineDateToModify routineDateToModify =
                taskRoutineDateCalculator.calculateRoutineDatesToModify(
                        dowithTask,
                        new TaskRoutineDateCalculator.RoutineCondition(
                                command.taskRoutineCondition().startDate(),
                                command.taskRoutineCondition().endDate(),
                                command.taskRoutineCondition().cycle(),
                                command.taskRoutineCondition().pattern(),
                                command.taskRoutineCondition().isExcludeHolidays()),
                        holidaySet);

        // 대상 dowith task에 New 루틴 생성
        dowithTask.createRoutine(
                command.taskRoutineCondition().startDate(),
                command.taskRoutineCondition().endDate(),
                command.taskRoutineCondition().cycle(),
                command.taskRoutineCondition().pattern(),
                command.taskRoutineCondition().isExcludeHolidays());

        // New routine 으로 교체
        dowithTasks.stream()
                .filter(e -> routineDateToModify.toUpdateDates().contains(e.getDate()))
                .forEach(e -> e.updateRoutine(dowithTask.getRoutine()));

        // 새 루틴 등록으로, 새 루틴 생성 + 연관 DowithTask 생성
        dowithTaskRepository.saveDowithTasks(DowithTask.of(dowithTask, routineDateToModify.toCreateDates()));

        // 삭제 필요한 routine 삭제
        dowithTaskRepository.delete(dowithTasks.stream()
                .filter(e -> routineDateToModify.toDeleteDates().contains(e.getDate()))
                .toList());

        return dowithTask;
    }
}
