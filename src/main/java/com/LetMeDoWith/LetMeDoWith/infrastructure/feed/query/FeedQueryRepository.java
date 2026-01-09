package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedQueryRepository {

    List<FeedDowithTaskQueryDto> getFeedbackAvailableDowithTasks(LocalDateTime referenceDateTime);
}