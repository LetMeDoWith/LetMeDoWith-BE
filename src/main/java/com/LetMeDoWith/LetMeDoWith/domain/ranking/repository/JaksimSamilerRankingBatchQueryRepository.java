package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingScoreQueryDto;
import java.time.LocalDateTime;
import java.util.List;

public interface JaksimSamilerRankingBatchQueryRepository {
    List<RankingScoreQueryDto> getRankingScores(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime);
}
