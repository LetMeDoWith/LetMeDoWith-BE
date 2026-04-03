package com.LetMeDoWith.LetMeDoWith.batch.dto;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;

public record CreateRankingResult(
        RankingTopic rankingTopic, RankingTopicRound rankingTopicRound, int rankingEntrySize) {}
