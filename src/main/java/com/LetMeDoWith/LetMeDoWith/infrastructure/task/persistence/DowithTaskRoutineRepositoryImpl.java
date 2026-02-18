package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskRoutineJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class DowithTaskRoutineRepositoryImpl implements DowithTaskRoutineRepository {

    private final DowithTaskRoutineJpaRepository jpaRepository;

    @Override
    public DowithTaskRoutine save(DowithTaskRoutine dowithTaskRoutine) {
        return jpaRepository.save(dowithTaskRoutine);
    }

    @Override
    public void delete(DowithTaskRoutine dowithTaskRoutine) {
        jpaRepository.delete(dowithTaskRoutine);
    }

    @Override
    public void delete(List<DowithTaskRoutine> dowithTaskRoutines) {
        jpaRepository.deleteAll(dowithTaskRoutines);
    }
}
