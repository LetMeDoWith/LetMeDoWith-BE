package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.notice.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {}
