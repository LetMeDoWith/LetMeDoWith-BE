package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence;

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
}
