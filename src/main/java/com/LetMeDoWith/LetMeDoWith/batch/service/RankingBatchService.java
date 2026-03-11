package com.LetMeDoWith.LetMeDoWith.batch.service;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankingBatchService {

    private final RankingRepository rankingRepository;

    /**
     * 활성 상태의 랭킹 토픽을 조회한다.
     *
     * @param title 랭킹 토픽 제목
     * @return 활성 토픽 Optional
     */
    @Transactional(readOnly = true)
    public Optional<RankingTopic> getRankingTopic(String title) {
        return rankingRepository.getRankingTopic(title, Yn.TRUE).map(topic -> {
            if (topic.getCurrentRound() != null) {
                topic.getCurrentRound().getRound();
            }
            return topic;
        });
    }

    @Transactional(readOnly = true)
    public Optional<RankingTopic> getRankingTopic(RankingTopicCode code) {
        return rankingRepository.getRankingTopic(code, Yn.TRUE);
    }

    /**
     * 토픽의 다음 회차를 생성해 저장한다.
     *
     * @param rankingTopic             집계 대상 토픽
     * @param currentRound             현재 회차(없으면 null)
     * @param aggregationStartDateTime 집계 시작 시각
     * @param aggregationEndDateTime   집계 종료 시각
     * @return 신규 생성된 회차 엔티티
     */
    @Transactional
    public RankingTopicRound createNextRound(
            RankingTopic rankingTopic,
            RankingTopicRound currentRound,
            LocalDateTime aggregationStartDateTime,
            LocalDateTime aggregationEndDateTime) {
        return rankingRepository.save(
                RankingTopicRound.nextOf(rankingTopic, currentRound, aggregationStartDateTime, aggregationEndDateTime));
    }

    /**
     * 실패 태스크 집계 결과를 회차 엔트리로 변환한다.
     *
     * @param topicId           랭킹 토픽 ID
     * @param previousRound     이전 회차 번호(없으면 null 가능)
     * @param rankingTopicRound 집계 회차
     * @param currentRankMap    이번 회차 순위 맵
     * @return 랭킹 엔트리 목록
     */
    @Transactional(readOnly = true)
    public List<RankingEntry> createRankingEntries(
            Long topicId, Long previousRound, RankingTopicRound rankingTopicRound, Map<String, Long> currentRankMap) {
        Map<String, Long> previousRankMap = createPreviousRankMap(topicId, previousRound);

        return currentRankMap.entrySet().stream()
                .map(entry -> RankingEntry.of(
                        rankingTopicRound, entry.getKey(), entry.getValue(), previousRankMap.get(entry.getKey())))
                .toList();
    }

    /**
     * 회차 엔트리를 신규 적재한다.
     *
     * @param rankingEntries 저장할 엔트리 목록
     */
    @Transactional
    public void saveRankingEntries(List<RankingEntry> rankingEntries) {
        if (!rankingEntries.isEmpty()) {
            rankingRepository.save(rankingEntries);
        }
    }

    /**
     * 토픽의 현재 회차 포인터를 갱신한다.
     *
     * @param rankingTopic      집계 대상 토픽
     * @param rankingTopicRound 현재 회차
     */
    @Transactional
    public void updateCurrentRound(RankingTopic rankingTopic, RankingTopicRound rankingTopicRound) {
        rankingTopic.updateCurrentRound(rankingTopicRound);
        rankingRepository.save(rankingTopic);
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
