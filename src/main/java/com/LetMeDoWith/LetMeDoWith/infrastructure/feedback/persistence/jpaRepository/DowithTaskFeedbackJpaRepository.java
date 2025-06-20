package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskFeedbackJpaRepository extends JpaRepository<DowithTaskFeedback, Long> {

    // This interface extends JpaRepository, which provides methods for CRUD operations.
    // Additional custom query methods can be defined here if needed.

}
