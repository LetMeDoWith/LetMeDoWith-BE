package com.LetMeDoWith.LetMeDoWith.application.feed.repository;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import java.util.List;

public interface FeedQueryRepository {

    List<FeedDowithTaskQueryDto> getFeedbackAvailableDowithTasks(
            String memberId, Long offset, int size);
}
