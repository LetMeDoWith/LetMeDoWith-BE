package com.LetMeDoWith.LetMeDoWith.application.feed.repository;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedQueryRepository {

    List<FeedDowithTaskQueryDto> getFeedbackAvailableDowithTasks(LocalDateTime referenceDateTime,
        Long offset, int size);
}