package com.LetMeDoWith.LetMeDoWith.application.ranking.service;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingEntryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingTopicRepository;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingTopicRoundRepository;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankingBatchService {

    private final RankingTopicRepository rankingTopicRepository;
    private final RankingTopicRoundRepository rankingTopicRoundRepository;
    private final RankingEntryRepository rankingEntryRepository;

    /**
     * 활성화된 랭킹 토픽을 제목으로 조회한다.
     *
     * @param title 랭킹 토픽 제목
     * @return 활성 토픽 Optional
     */
    @Transactional(readOnly = true)
    public Optional<RankingTopic> getActiveTopicByTitle(String title) {
        return rankingTopicRepository.getActiveTopicByTitle(title).map(topic -> {
            if (topic.getCurrentRound() != null) {
                topic.getCurrentRound().getRound();
            }
            return topic;
        });
    }

    /**
     * 토픽의 다음 회차를 생성해 저장한다.
     *
     * @param rankingTopic 집계 대상 토픽
     * @param currentRound 현재 회차(없으면 null)
     * @param aggregationStartDateTime 집계 시작 시각
     * @param aggregationEndDateTime 집계 종료 시각
     * @return 신규 생성된 회차 엔티티
     */
    @Transactional
    public RankingTopicRound createNextRound(
            RankingTopic rankingTopic,
            RankingTopicRound currentRound,
            LocalDateTime aggregationStartDateTime,
            LocalDateTime aggregationEndDateTime) {
        return rankingTopicRoundRepository.save(
                RankingTopicRound.nextOf(rankingTopic, currentRound, aggregationStartDateTime, aggregationEndDateTime));
    }

    /**
     * 이전 회차의 멤버별 순위 맵을 생성한다.
     *
     * @param topicId 랭킹 토픽 ID
     * @param previousRound 이전 회차 번호(없으면 null 가능)
     * @return memberId -> previousRank 맵
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getPreviousRankMap(Long topicId, Long previousRound) {
        if (previousRound == null) {
            return Map.of();
        }
        List<RankingEntry> previousEntries = rankingEntryRepository.getEntriesByTopicAndRound(topicId, previousRound);
        Map<String, Long> previousRankMap = new LinkedHashMap<>();
        previousEntries.forEach(entry -> previousRankMap.put(entry.getMemberId(), entry.getCurrentRank()));
        return previousRankMap;
    }

    /**
     * 회차 엔트리를 신규 적재한다.
     *
     * @param rankingEntries 저장할 엔트리 목록
     */
    @Transactional
    public void saveRankingEntries(List<RankingEntry> rankingEntries) {
        if (!rankingEntries.isEmpty()) {
            rankingEntryRepository.saveAll(rankingEntries);
        }
    }

    /**
     * 토픽의 현재 회차 포인터를 갱신한다.
     *
     * @param rankingTopic 집계 대상 토픽
     * @param rankingTopicRound 현재 회차
     */
    @Transactional
    public void updateCurrentRound(RankingTopic rankingTopic, RankingTopicRound rankingTopicRound) {
        rankingTopic.updateCurrentRound(rankingTopicRound);
        rankingTopicRepository.save(rankingTopic);
    }
}
