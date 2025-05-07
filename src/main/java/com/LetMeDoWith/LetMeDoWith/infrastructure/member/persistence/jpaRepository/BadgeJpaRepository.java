package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Badge;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeJpaRepository extends JpaRepository<Badge, Long> {
    
    Optional<Badge> findByIdAndBadgeStatus(Long id, BadgeStatus badgeStatus);
    
    Optional<Badge> findByName(String name);
}
