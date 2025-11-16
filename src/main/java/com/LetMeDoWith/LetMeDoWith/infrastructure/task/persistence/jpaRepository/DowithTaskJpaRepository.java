package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskJpaRepository extends JpaRepository<DowithTask, Long>, QDowithTaskRepository {

    Optional<DowithTask> findByDate(LocalDate date);

    List<DowithTask> findAllByDateIn(Set<LocalDate> dates);
}
