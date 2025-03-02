package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteDowithTaskService {
    
    private final DowithTaskRepository dowithTaskRepository;
    
    /**
     * 두윗모드 Task
     *
     * @param memberId
     * @param dowithTaskId
     */
    public void delete(Long memberId, Long dowithTaskId) {
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(dowithTaskId, memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        FailResponseStatus.DOWITH_TASK_NOT_EXIST));
        // TODO - routine에서 삭제
        dowithTaskRepository.delete(dowithTask);
    }
    
    public void deleteWithRoutines(Long memberId, Long dowithTaskId) {
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(dowithTaskId, memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        FailResponseStatus.DOWITH_TASK_NOT_EXIST));
        dowithTaskRepository.delete(dowithTasks);
        
    }
    
}
