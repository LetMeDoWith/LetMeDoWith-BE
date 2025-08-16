package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.HolidayRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskSummaryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TaskRoutineDateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeleteDowithTaskService {

    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskRoutineRepository dowithTaskRoutineRepository;
    private final TaskSummaryRepository taskSummaryRepository;
    private final HolidayRepository holidayRepository;

    private final TaskRoutineDateCalculator taskRoutineDateCalculator;

    /**
     * 두윗모드 Task 삭제
     *
     * @param memberId
     * @param dowithTaskId
     */
    @Transactional
    public void delete(String memberId, Long dowithTaskId) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        // 두윗모드 Task 사용 가능 개수 정책 무효화로 주석 처리
//        TaskSummary taskSummary = taskSummaryRepository
//                .getTaskSummary(memberId)
//                .orElseThrow(() -> new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR));

        if (!dowithTask.isDeleteAvail()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);

        if (dowithTask.isRoutine()) {
            // 삭제 대상이 루틴 설정되어있는데, routine이
            Set<Holiday> holidaySet = new HashSet<>();
            DowithTaskRoutine routine = dowithTask.getRoutine();
            if (dowithTask.isRoutineExcludeHolidays()) {
                holidaySet = holidayRepository.getHolidays(CountryCode.KR,
                        routine.getRangeStartDate(), routine.getRangeEndDate());
            }

            Set<LocalDate> routineDates = taskRoutineDateCalculator.calculateRoutineDates(dowithTask, holidaySet);
            if (routineDates.size() == 1 && routineDates.contains(dowithTask.getDate())) {
                dowithTaskRoutineRepository.delete(dowithTask.getRoutine());
            }
        }

        dowithTaskRepository.delete(dowithTask);
        // 두윗모드 Task 사용 가능 개수 정책 무효화로 주석 처리
//        taskSummary.plusRemainedDowithTaskCount(1);
    }

    /**
     * 두윗모드 Task + 루틴으로 등록된 모든 Task 삭제
     *
     * @param memberId
     * @param dowithTaskId
     */
    @Transactional
    public void deleteWithRoutines(String memberId, Long dowithTaskId) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        TaskSummary taskSummary = taskSummaryRepository
                .getTaskSummary(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR));

        int deletedDowithTaskCount = dowithTask.deleteWithRoutine(dowithTaskRepository, dowithTaskRoutineRepository);
//        taskSummary.plusRemainedDowithTaskCount(deletedDowithTaskCount);
    }
}
