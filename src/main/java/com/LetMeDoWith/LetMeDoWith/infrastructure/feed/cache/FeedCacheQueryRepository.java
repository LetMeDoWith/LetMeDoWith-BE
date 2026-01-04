package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.util.List;

public interface FeedCacheQueryRepository {

    List<FeedbackAvailableDowithTaskQueryDto> getFeedbackAvailableDowithTasks();
}
