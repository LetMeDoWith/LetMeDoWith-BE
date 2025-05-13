package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QDowithTaskRepository {

    Optional<DowithTask> findDowithTaskAggregate(Long id);

    Optional<DowithTask> findDowithTaskAggregate(Long id, String memberId);

    List<DowithTask> findAllDowithTaskAggregates(String memberId, LocalDate date);

    List<DowithTask> findAllDowithTaskAggregates(String memberId, Set<LocalDate> dates);

    List<DowithTask> findAllDowithTaskAggregates(DowithTaskRoutine dowithTaskRoutine);
}
