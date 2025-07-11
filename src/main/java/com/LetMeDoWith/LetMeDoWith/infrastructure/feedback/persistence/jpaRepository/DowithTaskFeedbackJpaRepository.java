package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskFeedbackJpaRepository extends JpaRepository<DowithTaskFeedback, Long> {

    Optional<DowithTaskFeedback> findTopByDowithTaskIdAndSenderIdOrderByCreatedAtDesc(
        Long dowithTaskId, String senderId);

    List<DowithTaskFeedback> findAllByDowithTaskId(Long dowithTaskId);

    List<DowithTaskFeedback> findAllByDowithTaskIdInAndMemberId(List<Long> dowithTaskIds,
        String memberId);
}