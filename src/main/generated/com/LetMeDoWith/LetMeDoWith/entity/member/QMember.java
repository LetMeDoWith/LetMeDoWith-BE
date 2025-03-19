package com.LetMeDoWith.LetMeDoWith.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {
    
    public static final QMember member = new QMember("member1");
    private static final long serialVersionUID = 495765476L;
    public final com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity _super = new com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity(
        this);
    
    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;
    
    //inherited
    public final StringPath createdBy = _super.createdBy;
    
    public final StringPath email = createString("email");
    
    public final ListPath<MemberFollow, QMemberFollow> followerMembers = this.<MemberFollow, QMemberFollow>createList(
        "followerMembers",
        MemberFollow.class,
        QMemberFollow.class,
        PathInits.DIRECT2);
    
    public final ListPath<MemberFollow, QMemberFollow> followingMembers = this.<MemberFollow, QMemberFollow>createList(
        "followingMembers",
        MemberFollow.class,
        QMemberFollow.class,
        PathInits.DIRECT2);
    
    public final NumberPath<Long> id = createNumber("id", Long.class);
    
    public final StringPath nickname = createString("nickname");
    
    public final StringPath profileImageUrl = createString("profileImageUrl");
    
    public final StringPath selfDescription = createString("selfDescription");
    
    public final ListPath<MemberSocialAccount, QMemberSocialAccount> socialAccountList = this.<MemberSocialAccount, QMemberSocialAccount>createList(
        "socialAccountList",
        MemberSocialAccount.class,
        QMemberSocialAccount.class,
        PathInits.DIRECT2);
    
    public final EnumPath<com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus> status = createEnum(
        "status",
        com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus.class);
    
    public final ListPath<MemberStatusHistory, QMemberStatusHistory> statusHistoryList = this.<MemberStatusHistory, QMemberStatusHistory>createList(
        "statusHistoryList",
        MemberStatusHistory.class,
        QMemberStatusHistory.class,
        PathInits.DIRECT2);
    
    public final EnumPath<com.LetMeDoWith.LetMeDoWith.enums.member.MemberType> type = createEnum(
        "type",
        com.LetMeDoWith.LetMeDoWith.enums.member.MemberType.class);
    
    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;
    
    //inherited
    public final StringPath updatedBy = _super.updatedBy;
    
    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }
    
    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }
    
    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }
    
}