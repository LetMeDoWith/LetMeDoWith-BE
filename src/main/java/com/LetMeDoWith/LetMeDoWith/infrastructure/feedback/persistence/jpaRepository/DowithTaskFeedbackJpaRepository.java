package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DowithTaskFeedbackJpaRepository extends JpaRepository<DowithTaskFeedback, Long> {

    Optional<DowithTaskFeedback> findTopByDowithTaskIdAndSenderMemberIdOrderByCreatedAtDesc(
            Long dowithTaskId, String senderMemberId);

    long countByDowithTaskIdAndSenderMemberId(Long dowithTaskId, String senderMemberId);

    List<DowithTaskFeedback> findAllByDowithTaskId(Long dowithTaskId);

    List<DowithTaskFeedback> findAllByDowithTaskIdInAndReceiverMemberId(
            List<Long> dowithTaskIds, String receiverMemberId);

    List<DowithTaskFeedback> findAllByIdInAndReceiverMemberId(List<Long> ids, String receiverMemberId);
}
