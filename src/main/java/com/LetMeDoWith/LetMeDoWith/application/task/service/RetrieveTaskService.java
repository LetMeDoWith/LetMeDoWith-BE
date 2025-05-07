package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.domain.task.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.TodoTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskQueryRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveTaskService {
    
    private final TaskQueryRepository taskQueryRepository;
    
    public List<TodoTaskQueryDto> retrieveTodoTasks(Long memberId, LocalDate startDate,
                                                    LocalDate endDate) {
        
        return taskQueryRepository.getTodoTasks(memberId,
                                                startDate,
                                                endDate);
    }
    
    public List<DowithTaskQueryDto> retrieveDowithTasks(Long memberId, LocalDate startDate,
                                                        LocalDate endDate) {
        
        return taskQueryRepository.getDowithTasks(memberId,
                                                  startDate,
                                                  endDate);
        
    }
    
}
