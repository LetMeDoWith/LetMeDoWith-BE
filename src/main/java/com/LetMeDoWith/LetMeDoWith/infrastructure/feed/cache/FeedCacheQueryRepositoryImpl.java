package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicy;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.redis.RedisOperator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("!dev")
public class FeedCacheQueryRepositoryImpl implements FeedCacheQueryRepository {

    private final RedisOperator redisOperator;

    @Override
    public List<Long> getFeedbackAvailableDowithTaskIds() {
        return redisOperator.getList(CachePolicy.DOWITH_TASK_IDS, "", 0, -1, Long.class);
    }

    @Override
    public List<FeedbackAvailableDowithTaskQueryDto> getFeedbackAvailableDowithTasks(
        List<Long> ids) {
        return redisOperator.getHashes(
            CachePolicy.DOWITH_TASK,
            ids.stream().map(String::valueOf).toList(),
            FeedbackAvailableDowithTaskQueryDto.class);
    }
}