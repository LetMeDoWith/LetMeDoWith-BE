package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache;

import com.LetMeDoWith.LetMeDoWith.common.redis.StorePolicy;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.redis.RedisOperator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedCacheCommandRepositoryImpl implements FeedCacheCommandRepository {

    private final RedisOperator redisOperator;

    @Override
    public void refreshFeedbackAvailableDowithTasks(List<FeedDowithTaskQueryDto> dowithTasks) {
        List<Long> ids = dowithTasks.stream().map(FeedDowithTaskQueryDto::id).toList();

        // 잔소리 대상 두윗 상세 정보 Redis 적재
        redisOperator.putHashes(StorePolicy.FEEDBACK_AVAILABLE_DOWITH_TASKS, dowithTasks, dto -> dto.id()
                .toString());

        // 잔소리 대상 두윗 ID 목록 Redis 적재
        if (ids.isEmpty()) {
            redisOperator.delete(StorePolicy.LAZY_DOWITH_TASK_IDS, "");
        } else {
            // id 인덱스는 atomic rename
            String tempKeySuffix = "temp_" + SystemTimeUtil.nowTime();
            redisOperator.pushRightAll(StorePolicy.LAZY_DOWITH_TASK_IDS, tempKeySuffix, ids);
            redisOperator.rename(StorePolicy.LAZY_DOWITH_TASK_IDS, tempKeySuffix, "");
        }
    }
}
