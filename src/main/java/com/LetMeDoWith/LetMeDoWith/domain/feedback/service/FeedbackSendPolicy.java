package com.LetMeDoWith.LetMeDoWith.domain.feedback.service;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FeedbackSendPolicy {

    private static final int FREE_THRESHOLD = 5;

    /**
     * 특정 두윗에 대해 발신자가 잔소리를 보낼 수 있는지 판단한다.
     * feedbackCount와 latestFeedback은 반드시 동일한 (두윗, 발신자) 단위로 집계된 값이어야 한다.
     * 현재 정책: 5회까지는 제약 없음, 이후 1분에 한 번.
     *
     * @param feedbackCount  해당 두윗에 발신자가 보낸 잔소리 누적 횟수
     * @param latestFeedback 해당 두윗에 발신자가 마지막으로 보낸 잔소리
     * @param now            현재 시각
     */
    public boolean canSend(long feedbackCount, Optional<DowithTaskFeedback> latestFeedback, LocalDateTime now) {
        if (feedbackCount < FREE_THRESHOLD) {
            return true;
        }
        return latestFeedback
                .map(f -> f.getCreatedAt().plusMinutes(1).isBefore(now))
                .orElse(true);
    }
}
