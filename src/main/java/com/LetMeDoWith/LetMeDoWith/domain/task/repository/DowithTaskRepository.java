package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DowithTaskRepository {

    DowithTask getReferenceById(Long id);

    Optional<DowithTask> getDowithTask(Long id);

    Optional<DowithTask> getDowithTask(Long id, String memberId);

    List<DowithTask> getDowithTasks(String memberId, LocalDate date);

    List<DowithTask> getDowithTasks(String memberId, Set<LocalDate> dates);

    List<DowithTask> getDowithTasks(DowithTaskRoutine dowithTaskRoutine);

    DowithTask saveDowithTask(DowithTask dowithTask);

    List<DowithTask> saveDowithTasks(List<DowithTask> dowithTasks);

    void delete(DowithTask dowithTask);

    void delete(List<DowithTask> dowithTasks);

    long countByStatus(DowithTaskStatus status);

    void flush();
}
