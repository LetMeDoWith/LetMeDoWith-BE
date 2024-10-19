package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.DowithTask;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QDowithTaskRepository {

  Optional<DowithTask> findJoinRoutineAndConfirm(Long id);
  List<DowithTask> findJoinRoutineAndConfirm(LocalDate date);
  Optional<DowithTask> findJoinRoutineAndConfirm(Long id, Long memberId);

}