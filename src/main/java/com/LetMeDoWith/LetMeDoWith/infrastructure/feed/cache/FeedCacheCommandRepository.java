package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import java.util.List;

public interface FeedCacheCommandRepository {

    void refreshFeedbackAvailableDowithTasks(List<FeedDowithTaskQueryDto> dowithTasks);
}
