package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.util.List;

public interface FeedCacheRepository {

    List<Long> getFeedbackAvailableDowithTaskIds();

    List<FeedbackAvailableDowithTaskQueryDto> getFeedbackAvailableDowithTasks(List<Long> ids);
}
