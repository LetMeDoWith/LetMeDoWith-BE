package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskLikeRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class DowithTaskLikeRepositoryImpl implements DowithTaskLikeRepository {

    private final DowithTaskLikeJpaRepository jpaRepository;

    @Override
    public long countDowithTaskLikesByDowithTaskId(Long dowithTaskId) {
        return jpaRepository.countByDowithTask_Id(dowithTaskId);
    }

    @Override
    public Optional<DowithTaskLike> getDowithTaskLike(String memberId, DowithTask dowithTask) {
        return jpaRepository.findByMemberIdAndDowithTask(memberId, dowithTask);
    }

    @Override
    public int saveIgnore(DowithTaskLike dowithTaskLike) {
        dowithTaskLike.setCreateAuditingInfo();
        return jpaRepository.saveIgnore(dowithTaskLike);
    }

    @Override
    public long delete(Long dowithTaskId, String memberId) {
        long deletedRows = jpaRepository.deleteByDowithTask_IdAndMemberId(dowithTaskId, memberId);
        return deletedRows;
    }
}
