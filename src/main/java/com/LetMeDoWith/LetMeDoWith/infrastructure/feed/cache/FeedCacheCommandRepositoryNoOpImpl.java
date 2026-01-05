package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FeedCacheCommandRepositoryNoOpImpl implements FeedCacheCommandRepository {

    @Override
    public void refreshFeedbackAvailableDowithTasks(List<FeedbackAvailableDowithTaskQueryDto> dowithTasks) {}
}
