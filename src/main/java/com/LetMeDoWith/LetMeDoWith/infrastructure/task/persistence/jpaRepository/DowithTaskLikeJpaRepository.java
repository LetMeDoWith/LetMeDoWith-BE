package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DowithTaskLikeJpaRepository extends JpaRepository<DowithTaskLike, Long> {
    long countByDowithTask_Id(Long dowithTaskId);

    boolean existsByDowithTask_IdAndMemberId(Long dowithTaskId, String memberId);

    Optional<DowithTaskLike> findByMemberIdAndDowithTask(String memberId, DowithTask dowithTask);

    long deleteByDowithTask_IdAndMemberId(Long dowithTaskId, String memberId);

    @Modifying
    @Query(
            value =
                    """
                            INSERT IGNORE INTO dowith_task_like (member_id, dowith_task_id, created_at, updated_at, created_by, updated_by)
                            VALUES (:#{#dowithTaskLike.memberId}, :#{#dowithTaskLike.dowithTask.id}, :#{#dowithTaskLike.createdAt}, :#{#dowithTaskLike.updatedAt}, :#{#dowithTaskLike.createdBy}, :#{#dowithTaskLike.updatedBy})
                            """,
            nativeQuery = true)
    int saveIgnore(DowithTaskLike dowithTaskLike);
}
