package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;

import java.util.Optional;

public interface TaskSummaryRepository {

    Optional<TaskSummary> getTaskSummary(String memberId);

}
