package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskSummaryJpaRepository extends JpaRepository<TaskSummary, Long> {
    Optional<TaskSummary> findByMemberId(String memberId);
}
