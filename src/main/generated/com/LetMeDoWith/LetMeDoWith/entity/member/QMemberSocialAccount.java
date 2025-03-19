package com.LetMeDoWith.LetMeDoWith.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberSocialAccount is a Querydsl query type for MemberSocialAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberSocialAccount extends EntityPathBase<MemberSocialAccount> {

    private static final long serialVersionUID = 1715576956L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberSocialAccount memberSocialAccount = new QMemberSocialAccount("memberSocialAccount");

    public final com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity _super = new com.LetMeDoWith.LetMeDoWith.entity.QBaseAuditEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final EnumPath<com.LetMeDoWith.LetMeDoWith.enums.SocialProvider> provider = createEnum("provider", com.LetMeDoWith.LetMeDoWith.enums.SocialProvider.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QMemberSocialAccount(String variable) {
        this(MemberSocialAccount.class, forVariable(variable), INITS);
    }

    public QMemberSocialAccount(Path<? extends MemberSocialAccount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberSocialAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberSocialAccount(PathMetadata metadata, PathInits inits) {
        this(MemberSocialAccount.class, metadata, inits);
    }

    public QMemberSocialAccount(Class<? extends MemberSocialAccount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

