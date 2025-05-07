package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskRoutineJpaRepository extends JpaRepository<DowithTaskRoutine, Long> {

}
