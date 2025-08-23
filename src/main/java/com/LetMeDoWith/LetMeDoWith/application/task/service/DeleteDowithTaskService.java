package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.HolidayRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskSummaryRepository;
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
            // 루틴이 있는데, 루틴에 해당되는 마지막 DowithTask인 경우 routine 삭제
            List<DowithTask> dowithTasks = dowithTaskRepository.getDowithTasks(dowithTask.getRoutine());
            if (dowithTasks.size() == 1) dowithTaskRoutineRepository.delete(dowithTask.getRoutine());
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

        if (!dowithTask.isDeleteAvail()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        if (!dowithTask.isRoutine()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);

        Set<Holiday> holidaySet = new HashSet<>();
        if (dowithTask.isRoutineExcludeHolidays()) {
            holidaySet = holidayRepository.getHolidays(
                    CountryCode.KR,
                    dowithTask.getRoutine().getRangeStartDate(),
                    dowithTask.getRoutine().getRangeEndDate());
        }
        Set<LocalDate> toDeleteRoutineDates =
                taskRoutineDateCalculator.calculateEditableRoutineDates(dowithTask, holidaySet);

        List<DowithTask> dowithTasks = dowithTaskRepository.getDowithTasks(dowithTask.getRoutine());
        Map<Boolean, List<DowithTask>> partition = dowithTasks.stream()
                .collect(Collectors.partitioningBy(task -> toDeleteRoutineDates.contains(task.getDate())));

        dowithTaskRepository.delete(partition.get(Boolean.TRUE));
        partition.get(Boolean.FALSE).forEach(DowithTask::deleteRoutine);
        // 두윗모드 Task 사용 가능 개수 정책 무효화로 주석 처리
        //        TaskSummary taskSummary = taskSummaryRepository
        //                .getTaskSummary(memberId)
        //                .orElseThrow(() -> new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR));
        //        taskSummary.plusRemainedDowithTaskCount(deletedDowithTaskCount);
    }
}
