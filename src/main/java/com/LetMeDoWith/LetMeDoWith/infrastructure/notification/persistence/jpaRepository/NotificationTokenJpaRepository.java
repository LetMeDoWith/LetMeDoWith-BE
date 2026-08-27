package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenJpaRepository extends JpaRepository<NotificationToken, Long> {
    Optional<NotificationToken> findByMemberId(String memberId);

    List<NotificationToken> findByMemberIdInAndIsExpired(List<String> memberIds, Yn isExpired);
}
