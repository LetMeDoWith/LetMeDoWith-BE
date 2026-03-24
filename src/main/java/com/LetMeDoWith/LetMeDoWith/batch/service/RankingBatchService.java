package com.LetMeDoWith.LetMeDoWith.batch.service;

import com.LetMeDoWith.LetMeDoWith.batch.dto.CreateRankingResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingRepository;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankingBatchService {

    private final RankingRepository rankingRepository;

    @Transactional
    public CreateRankingResult createRanking(
            RankingTopicCode topicCode,
            LocalDateTime aggregationStartDateTime,
            LocalDateTime aggregationEndDateTime,
            Map<String, Long> memberIdRankMap) {
        // 1. RankingTopic 조회 및 이전 Round 조회
        RankingTopic rankingTopic = this.rankingRepository
                .getRankingTopic(topicCode, Yn.TRUE)
                .orElseThrow(() ->
                        new RuntimeException("RankingTopicCode " + topicCode + "에 해당하는 RankingTopic이 존재하지 않습니다."));
        RankingTopicRound previousRound = rankingTopic.getCurrentRound();

        // 2. 다음 Round 생성
        RankingTopicRound newRound = rankingRepository.save(RankingTopicRound.nextOf(
                rankingTopic, previousRound, aggregationStartDateTime, aggregationEndDateTime));

        // 3. RankingEntries 생성
        Map<String, Long> previousRankMap =
                createPreviousRankMap(rankingTopic.getId(), previousRound == null ? null : previousRound.getRound());
        List<RankingEntry> rankingEntries = memberIdRankMap.entrySet().stream()
                .map(entry -> RankingEntry.of(
                        newRound, entry.getKey(), entry.getValue(), previousRankMap.getOrDefault(entry.getKey(), null)))
                .toList();
        rankingRepository.save(rankingEntries);

        // 5. RankingTopic 현재 Round 업데이트
        rankingTopic.updateCurrentRound(newRound);
        rankingRepository.save(rankingTopic);

        return new CreateRankingResult(rankingTopic, newRound, rankingEntries.size());
    }

    private Map<String, Long> createPreviousRankMap(Long topicId, Long previousRound) {
        if (previousRound == null) {
            return Map.of();
        }

        List<RankingEntry> previousEntries = rankingRepository.getRankingEntries(topicId, previousRound);
        Map<String, Long> previousRankMap = new LinkedHashMap<>();
        previousEntries.forEach(entry -> previousRankMap.put(entry.getMemberId(), entry.getCurrentRank()));
        return previousRankMap;
    }
}
