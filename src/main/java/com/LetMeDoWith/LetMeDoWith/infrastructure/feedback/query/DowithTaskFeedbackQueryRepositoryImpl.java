package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QDowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
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
                    dowithTaskFeedback.taskFeedbackTemplateId,
                    member.id,
                    member.nickname,
                    member.profileImageUrl,
                    new CaseBuilder()
                        .when(
                            dowithTaskFeedback.isChecked.eq(
                                com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn.TRUE))
                        .then(true)
                        .otherwise(false)))
            .from(dowithTaskFeedback)
            .leftJoin(member)
            .fetchJoin()
            .on(dowithTaskFeedback.senderMemberId.eq(member.id))
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
                    dowithTaskFeedback.taskFeedbackTemplateId,
                    member.id,
                    member.nickname,
                    member.profileImageUrl,
                    new CaseBuilder()
                        .when(dowithTaskFeedback.isChecked.eq(Yn.TRUE))
                        .then(true)
                        .otherwise(false)))
            .from(dowithTaskFeedback)
            .leftJoin(member)
            .fetchJoin()
            .on(dowithTaskFeedback.senderMemberId.eq(member.id))
            .where(dowithTaskFeedback.senderMemberId.eq(senderId))
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
                    dowithTaskFeedback.taskFeedbackTemplateId,
                    member.id,
                    member.nickname,
                    member.profileImageUrl,
                    new CaseBuilder()
                        .when(dowithTaskFeedback.isChecked.eq(Yn.TRUE))
                        .then(true)
                        .otherwise(false)))
            .from(dowithTaskFeedback)
            .leftJoin(member)
            .fetchJoin()
            .on(dowithTaskFeedback.senderMemberId.eq(member.id))
            .where(dowithTaskFeedback.receiverMemberId.eq(receiverId))
            .fetch();
    }
}