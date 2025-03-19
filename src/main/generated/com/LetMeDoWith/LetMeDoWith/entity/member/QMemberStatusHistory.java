package com.LetMeDoWith.LetMeDoWith.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberStatusHistory is a Querydsl query type for MemberStatusHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberStatusHistory extends EntityPathBase<MemberStatusHistory> {

    private static final long serialVersionUID = -872049986L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberStatusHistory memberStatusHistory = new QMemberStatusHistory("memberStatusHistory");

    public final com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity _super = new com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final EnumPath<com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus> status = createEnum("status", com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus.class);

    public final DateTimePath<java.time.LocalDateTime> statusChangedAt = createDateTime("statusChangedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> statusEndAt = createDateTime("statusEndAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QMemberStatusHistory(String variable) {
        this(MemberStatusHistory.class, forVariable(variable), INITS);
    }

    public QMemberStatusHistory(Path<? extends MemberStatusHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberStatusHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberStatusHistory(PathMetadata metadata, PathInits inits) {
        this(MemberStatusHistory.class, metadata, inits);
    }

    public QMemberStatusHistory(Class<? extends MemberStatusHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

