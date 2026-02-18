package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskSummaryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskSummaryJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class TaskSummaryRepositoryImpl implements TaskSummaryRepository {

    private final TaskSummaryJpaRepository taskSummaryJpaRepository;

    @Override
    public TaskSummary save(TaskSummary taskSummary) {
        return taskSummaryJpaRepository.save(taskSummary);
    }

    @Override
    public Optional<TaskSummary> getTaskSummary(String memberId) {
        return taskSummaryJpaRepository.findByMemberId(memberId);
    }
}
