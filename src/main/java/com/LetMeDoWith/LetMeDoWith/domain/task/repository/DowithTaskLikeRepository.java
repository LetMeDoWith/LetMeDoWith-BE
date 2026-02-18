package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;

public interface DowithTaskLikeRepository {
    long countDowithTaskLikesByDowithTaskId(Long dowithTaskId);

    DowithTaskLike save(DowithTaskLike dowithTaskLike);

    void flush();

    boolean existsByDowithTaskIdAndMemberId(Long dowithTaskId, String memberId);
}
