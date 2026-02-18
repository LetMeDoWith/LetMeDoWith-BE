package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import java.util.Optional;

public interface DowithTaskLikeRepository {
    long countDowithTaskLikesByDowithTaskId(Long dowithTaskId);

    Optional<DowithTaskLike> getDowithTaskLike(String memberId, DowithTask dowithTask);

    DowithTaskLike save(DowithTaskLike dowithTaskLike);

    boolean delete(DowithTaskLike dowithTaskLike);
}
