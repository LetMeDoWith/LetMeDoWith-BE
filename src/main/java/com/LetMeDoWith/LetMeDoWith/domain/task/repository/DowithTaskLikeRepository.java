package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import java.util.Optional;

public interface DowithTaskLikeRepository {
    long countDowithTaskLikesByDowithTaskId(Long dowithTaskId);

    Optional<DowithTaskLike> getDowithTaskLike(String memberId, DowithTask dowithTask);

    int saveIgnore(DowithTaskLike dowithTaskLike);

    long delete(Long dowithTaskId, String memberId);
}
