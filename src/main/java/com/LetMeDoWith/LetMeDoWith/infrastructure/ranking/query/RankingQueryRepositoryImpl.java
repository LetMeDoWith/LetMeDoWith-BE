package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.QRankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.QRankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.QRankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingTopicsQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RankingQueryRepositoryImpl implements RankingQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QRankingTopic qRankingTopic = QRankingTopic.rankingTopic;
    private final QRankingTopicRound qRankingTopicRound = QRankingTopicRound.rankingTopicRound;
    private final QRankingEntry qRankingEntry = QRankingEntry.rankingEntry;
    private final QMember member = QMember.member;

    @Override
    public List<RankingTopicsQueryDto> getRankingTopics() {
        return queryFactory
                .select(Projections.constructor(
                        RankingTopicsQueryDto.class,
                        qRankingTopic.id,
                        qRankingTopic.title,
                        qRankingTopic.description,
                        qRankingTopicRound.round))
                .from(qRankingTopic)
                .leftJoin(qRankingTopicRound)
                .on(qRankingTopic.currentRound.id.eq(qRankingTopicRound.id))
                .where(qRankingTopic.isActive.eq(Yn.TRUE))
                .fetch();
    }

    @Override
    public boolean existsTopicRound(Long rankingTopicId, Long round) {
        Integer exists = queryFactory
                .selectOne()
                .from(qRankingTopicRound)
                .where(qRankingTopicRound.rankingTopic.id.eq(rankingTopicId).and(qRankingTopicRound.round.eq(round)))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public List<RankingsQueryDto> getRankingsByTopicId(Long rankingTopicId, Long round, Integer limit) {
        return queryFactory
                .select(Projections.constructor(
                        RankingsQueryDto.class,
                        qRankingTopicRound.round,
                        qRankingTopic.id,
                        qRankingTopic.title,
                        qRankingEntry.currentRank,
                        qRankingEntry.previousRank,
                        qRankingEntry.memberId,
                        member.nickname,
                        member.profileImageUrl))
                .from(qRankingEntry)
                .leftJoin(qRankingTopicRound)
                .on(qRankingEntry.rankingTopicRound.id.eq(qRankingTopicRound.id))
                .leftJoin(qRankingTopic)
                .on(qRankingTopicRound.rankingTopic.id.eq(qRankingTopic.id))
                .leftJoin(member)
                .on(qRankingEntry.memberId.eq(member.id))
                .where(qRankingTopic.id.eq(rankingTopicId).and(qRankingTopicRound.round.eq(round)))
                .orderBy(qRankingEntry.currentRank.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<RankingsQueryDto> getMyRanking(String memberId, Long rankingTopicId, Long round) {
        RankingsQueryDto ranking = queryFactory
                .select(Projections.constructor(
                        RankingsQueryDto.class,
                        qRankingTopicRound.round,
                        qRankingTopic.id,
                        qRankingTopic.title,
                        qRankingEntry.currentRank,
                        qRankingEntry.previousRank,
                        qRankingEntry.memberId,
                        member.nickname,
                        member.profileImageUrl))
                .from(qRankingEntry)
                .leftJoin(qRankingTopicRound)
                .on(qRankingEntry.rankingTopicRound.id.eq(qRankingTopicRound.id))
                .leftJoin(qRankingTopic)
                .on(qRankingTopicRound.rankingTopic.id.eq(qRankingTopic.id))
                .leftJoin(member)
                .on(qRankingEntry.memberId.eq(member.id))
                .where(qRankingTopic
                        .id
                        .eq(rankingTopicId)
                        .and(qRankingTopicRound.round.eq(round))
                        .and(qRankingEntry.memberId.eq(memberId)))
                .fetchOne();

        return Optional.ofNullable(ranking);
    }
}
