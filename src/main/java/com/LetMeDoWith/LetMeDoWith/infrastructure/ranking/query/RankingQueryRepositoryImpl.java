package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.query;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.QRanking;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.QRankingTopic;
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
    private final QRanking qRanking = QRanking.ranking;
    private final QMember member = QMember.member;

    @Override
    public List<RankingTopicsQueryDto> getRankingTopics() {
        return queryFactory
                .select(Projections.constructor(
                        RankingTopicsQueryDto.class, qRankingTopic.id, qRankingTopic.title, qRankingTopic.description))
                .from(qRankingTopic)
                .fetch();
    }

    @Override
    public List<RankingsQueryDto> getRankingsByTopicId(
            Long rankingTopicId, Integer year, Integer month, Integer week, Integer limit) {
        return queryFactory
                .select(Projections.constructor(
                        RankingsQueryDto.class,
                        qRanking.year,
                        qRanking.month,
                        qRanking.week,
                        qRanking.rankingTopic.id,
                        qRanking.rankingTopic.title,
                        qRanking.currentRank,
                        qRanking.previousRank,
                        qRanking.memberId,
                        member.nickname,
                        member.profileImageUrl))
                .from(qRanking)
                .leftJoin(member)
                .on(qRanking.memberId.eq(member.id))
                .where(qRanking.rankingTopic
                        .id
                        .eq(rankingTopicId)
                        .and(qRanking.year.eq(year))
                        .and(qRanking.month.eq(month))
                        .and(qRanking.week.eq(week)))
                .orderBy(qRanking.currentRank.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<RankingsQueryDto> getMyRanking(
            String memberId, Long rankingTopicId, Integer year, Integer month, Integer week) {
        RankingsQueryDto ranking = queryFactory
                .select(Projections.constructor(
                        RankingsQueryDto.class,
                        qRanking.year,
                        qRanking.month,
                        qRanking.week,
                        qRanking.rankingTopic.id,
                        qRanking.rankingTopic.title,
                        qRanking.currentRank,
                        qRanking.previousRank,
                        qRanking.memberId,
                        member.nickname,
                        member.profileImageUrl))
                .from(qRanking)
                .leftJoin(member)
                .on(qRanking.memberId.eq(member.id))
                .where(qRanking.rankingTopic
                        .id
                        .eq(rankingTopicId)
                        .and(qRanking.year.eq(year))
                        .and(qRanking.month.eq(month))
                        .and(qRanking.week.eq(week))
                        .and(qRanking.memberId.eq(memberId)))
                .fetchOne();
                
        return Optional.ofNullable(ranking);
    }
}
