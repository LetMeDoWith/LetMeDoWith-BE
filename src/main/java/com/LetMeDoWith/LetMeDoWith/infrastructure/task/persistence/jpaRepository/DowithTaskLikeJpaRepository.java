package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskLikeJpaRepository extends JpaRepository<DowithTaskLike, Long> {
    long countByDowithTask_Id(Long dowithTaskId);

    boolean existsByDowithTask_IdAndMemberId(Long dowithTaskId, String memberId);

    Optional<DowithTaskLike> findByMemberIdAndDowithTask(String memberId, DowithTask dowithTask);

    long deleteByDowithTask_IdAndMemberId(Long dowithTaskId, String memberId);
}
