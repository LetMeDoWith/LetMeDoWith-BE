package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QDowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DowithTaskFeedbackQueryRepositoryImpl implements DowithTaskFeedbackQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    private final QDowithTaskFeedback dowithTaskFeedback = QDowithTaskFeedback.dowithTaskFeedback;
    private final QMember member = QMember.member;
    
    @Override
    public List<DowithTaskFeedbackQueryDto> findAllByTaskId(Long taskId) {
        return queryFactory
            .select(
                Projections.constructor(
                    DowithTaskFeedbackQueryDto.class,
                    dowithTaskFeedback.id,
                    dowithTaskFeedback.dowithTaskId,
                    member.id,
                    member.nickname,
                    member.profileImageUrl,
                    dowithTaskFeedback.isChecked))
            .from(dowithTaskFeedback)
            .leftJoin(member)
            .fetchJoin()
            .on(dowithTaskFeedback.senderId.eq(member.id))
            .where(dowithTaskFeedback.dowithTaskId.eq(taskId))
            .fetch();
    }
    
    @Override
    public List<DowithTaskFeedbackQueryDto> findAllBySenderId(String senderId) {
        return queryFactory
            .select(
                Projections.constructor(
                    DowithTaskFeedbackQueryDto.class,
                    dowithTaskFeedback.id,
                    dowithTaskFeedback.dowithTaskId,
                    member.id,
                    member.nickname,
                    member.profileImageUrl,
                    dowithTaskFeedback.isChecked))
            .from(dowithTaskFeedback)
            .leftJoin(member)
            .fetchJoin()
            .on(dowithTaskFeedback.senderId.eq(member.id))
            .where(dowithTaskFeedback.senderId.eq(senderId))
            .fetch();
    }
    
    @Override
    public List<DowithTaskFeedbackQueryDto> findAllByReceiverId(String receiverId) {
        return queryFactory
            .select(
                Projections.constructor(
                    DowithTaskFeedbackQueryDto.class,
                    dowithTaskFeedback.id,
                    dowithTaskFeedback.dowithTaskId,
                    member.id,
                    member.nickname,
                    member.profileImageUrl,
                    dowithTaskFeedback.isChecked))
            .from(dowithTaskFeedback)
            .leftJoin(member)
            .fetchJoin()
            .on(dowithTaskFeedback.senderId.eq(member.id))
            .where(dowithTaskFeedback.receiverId.eq(receiverId))
            .fetch();
    }
}