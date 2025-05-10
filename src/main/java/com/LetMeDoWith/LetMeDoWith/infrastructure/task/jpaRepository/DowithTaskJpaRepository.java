package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskJpaRepository
        extends JpaRepository<DowithTask, Long>, QDowithTaskRepository {

    Optional<DowithTask> findByDate(LocalDate date);
}
