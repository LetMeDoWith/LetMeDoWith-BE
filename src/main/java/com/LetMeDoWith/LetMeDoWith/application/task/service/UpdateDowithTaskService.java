package com.LetMeDoWith.LetMeDoWith.application.task.service;

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
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.*;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TaskRoutineDateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

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

        TaskCategory taskCategory = taskCategoryRepository
                .getActiveTaskCategory(command.taskCategoryId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (dowithTask.isContentsEditable()) {
            dowithTask.updateContents(command.title(), taskCategory.getId(), command.date(), command.startTime());
        } else {
            dowithTask.updateContents(command.title(), taskCategory.getId());
        }

        Set<Holiday> holidaySet = new HashSet<>();
        if (command.taskRoutineCondition().isExcludeHolidays()) {
            holidaySet = holidayRepository.getHolidays(
                    CountryCode.KR,
                    command.taskRoutineCondition().startDate(),
                    command.taskRoutineCondition().endDate()
            );
        }

        TaskRoutineCondition taskRoutineCondition = command.taskRoutineCondition();
        dowithTask.createRoutine(
                taskRoutineCondition.startDate(),
                taskRoutineCondition.endDate(),
                taskRoutineCondition.cycle(),
                taskRoutineCondition.pattern(),
                taskRoutineCondition.isExcludeHolidays()
        );
        Set<LocalDate> routineDates = taskRoutineDateCalculator.calculateRoutineDates(dowithTask, holidaySet);

        dowithTaskRepository.saveDowithTask(dowithTask);
        dowithTaskRepository.saveDowithTasks(DowithTask.of(dowithTask, routineDates));

    }

    /**
     * 두윗모드Task 내용만 수정
     *
     * @param memberId
     * @param command
     */
    @Transactional
    public DowithTask updateDowithTaskContentsOnly(UpdateDowithTaskContentsOnlyCommand command) {

        String memberId = AuthUtil.getMemberId();
        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(command.dowithTaskId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        TaskCategory taskCategory = taskCategoryRepository
                .getActiveTaskCategory(command.taskCategoryId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (dowithTask.isRoutine()) {

            // TODO- 수정 대상이 어디까지인지 해당 정책 확인 필요 to 기획
            if (dowithTask.isContentsEditable()) { // TODO- 메서드명 명확하게 수정
                dowithTask.updateContentsWithRoutine(
                        command.title(),
                        taskCategory.getId(),
                        command.date(),
                        command.startTime(),
                        dowithTaskRepository);
            } else {
                dowithTask.updateContentsWithRoutine(command.title(), taskCategory.getId(), dowithTaskRepository);
            }

        } else {
            if (dowithTask.isContentsEditable()) {
                dowithTask.updateContents(command.title(), taskCategory.getId(), command.date(), command.startTime());
            } else {
                dowithTask.updateContents(command.title(), taskCategory.getId());
            }
        }

        return dowithTask;
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

        // 루틴 사용 개수 제한 정책 무효화로 주석 처리
//        final TaskSummary taskSummary =
//                taskSummaryRepository.getTaskSummary(memberId).orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (!dowithTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        List<DowithTask> dowithTasks = dowithTaskRepository.getDowithTasks(dowithTask.getRoutine());

        Set<Holiday> holidaySet = new HashSet<>();
        if (dowithTask.isRoutineExcludeHolidays()) {
            holidaySet = holidayRepository.getHolidays(CountryCode.KR,
                    DateTimeUtil.earlier(dowithTask.getRoutine().getRangeStartDate(), command.taskRoutineCondition().startDate()),
                    DateTimeUtil.earlier(dowithTask.getRoutine().getRangeEndDate(), command.taskRoutineCondition().endDate()));
        }

        // routine 수정
        dowithTask.updateRoutine(
                command.taskRoutineCondition().startDate(),
                command.taskRoutineCondition().endDate(),
                command.taskRoutineCondition().cycle(),
                command.taskRoutineCondition().pattern(),
                command.taskRoutineCondition().isExcludeHolidays()
        );

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
                                command.taskRoutineCondition().isExcludeHolidays()
                        ),
                        holidaySet);

        // 새 루틴 등록으로, 삭제할 루틴 일자 + 연관 DowithTask 삭제
        dowithTaskRepository.delete(dowithTasks.stream()
                .filter(e -> routineDateToModify.toDeleteDates().contains(e.getDate()))
                .toList());

        // 새 루틴 등록으로, 새 루틴 생성 + 연관 DowithTask 생성
        dowithTaskRepository.saveDowithTasks(
                DowithTask.of(dowithTask, routineDateToModify.toCreateDates())
        );

        return dowithTask;
    }
}
