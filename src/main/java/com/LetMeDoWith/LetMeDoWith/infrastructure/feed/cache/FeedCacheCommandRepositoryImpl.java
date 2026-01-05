package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicy;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.redis.RedisOperator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("!dev")
public class FeedCacheCommandRepositoryImpl implements FeedCacheCommandRepository {

    private final RedisOperator redisOperator;

    @Override
    public void refreshFeedbackAvailableDowithTasks(List<FeedbackAvailableDowithTaskQueryDto> dowithTasks) {
        List<Long> ids = dowithTasks.stream()
                .map(FeedbackAvailableDowithTaskQueryDto::id)
                .toList();

        // 잔소리 대상 두윗 상세 정보 Redis 적재
        redisOperator.putHashes(
                CachePolicy.DOWITH_TASK, dowithTasks, dto -> dto.id().toString());

        // 잔소리 대상 두윗 ID 목록 Redis 적재
        if (ids.isEmpty()) {
            redisOperator.delete(CachePolicy.DOWITH_TASK_IDS, "");
        } else {
            // id 인덱스는 atomic rename
            String tempKeySuffix = "temp_" + SystemTimeUtil.nowTime();
            redisOperator.pushRightAll(CachePolicy.DOWITH_TASK_IDS, tempKeySuffix, ids);
            redisOperator.rename(CachePolicy.DOWITH_TASK_IDS, tempKeySuffix, "");
        }
    }
}
