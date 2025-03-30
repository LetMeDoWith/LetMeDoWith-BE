package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.provider.TimeProvider;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDowithTaskService {
    
    private final TimeProvider timeProvider;
    
    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskRoutineRepository dowithTaskRoutineRepository;
    
    
    /**
     * 두윗모드 Task 삭제
     *
     * @param memberId
     * @param dowithTaskId
     */
    @Transactional
    public void delete(Long memberId, Long dowithTaskId) {
        
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(dowithTaskId, memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        FailResponseStatus.INVALID_REQUEST));
        
        dowithTask.delete(dowithTaskRepository, dowithTaskRoutineRepository, timeProvider);
        
    }
    
    /**
     * 두윗모드 Task 삭제 (루틴 포함)
     *
     * @param memberId
     * @param dowithTaskId
     */
    @Transactional
    public void deleteWithRoutines(Long memberId, Long dowithTaskId) {
        
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(dowithTaskId, memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        FailResponseStatus.DOWITH_TASK_NOT_EXIST));
        
        dowithTask.deleteWithRoutine(dowithTaskRepository,
                                     dowithTaskRoutineRepository,
                                     timeProvider);
        
    }
    
}
