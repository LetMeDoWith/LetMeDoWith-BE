package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedQueryRepository {

    List<FeedbackAvailableDowithTaskQueryDto> getFeedbackAvailableDowithTasks(
            LocalDateTime referenceDateTime, Long limit);

    List<FeedbackAvailableDowithTaskQueryDto> getFeedbackAvailableDowithTasks(LocalDateTime referenceDateTime);
}
