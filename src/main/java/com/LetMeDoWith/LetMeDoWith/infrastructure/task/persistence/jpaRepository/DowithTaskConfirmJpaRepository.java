package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskSuccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskConfirmJpaRepository extends JpaRepository<DowithTaskSuccess, Long> {}
