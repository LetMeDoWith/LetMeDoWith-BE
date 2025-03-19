package com.LetMeDoWith.LetMeDoWith.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberFollow is a Querydsl query type for MemberFollow
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberFollow extends EntityPathBase<MemberFollow> {

    private static final long serialVersionUID = 1466594485L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberFollow memberFollow = new QMemberFollow("memberFollow");

    public final com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity _super = new com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QMember followerMember;

    public final QMember followingMember;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QMemberFollow(String variable) {
        this(MemberFollow.class, forVariable(variable), INITS);
    }

    public QMemberFollow(Path<? extends MemberFollow> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberFollow(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberFollow(PathMetadata metadata, PathInits inits) {
        this(MemberFollow.class, metadata, inits);
    }

    public QMemberFollow(Class<? extends MemberFollow> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.followerMember = inits.isInitialized("followerMember") ? new QMember(forProperty("followerMember")) : null;
        this.followingMember = inits.isInitialized("followingMember") ? new QMember(forProperty("followingMember")) : null;
    }

}

