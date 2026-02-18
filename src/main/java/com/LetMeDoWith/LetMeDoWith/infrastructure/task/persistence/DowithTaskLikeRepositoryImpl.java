package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskLikeRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskLikeJpaRepository;
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
    public DowithTaskLike save(DowithTaskLike dowithTaskLike) {
        return jpaRepository.save(dowithTaskLike);
    }

    @Override
    public void flush() {
        jpaRepository.flush();
    }

    @Override
    public boolean existsByDowithTaskIdAndMemberId(Long dowithTaskId, String memberId) {
        return jpaRepository.existsByDowithTask_IdAndMemberId(dowithTaskId, memberId);
    }
}
