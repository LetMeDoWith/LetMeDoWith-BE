package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.cache;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * Redis가 없는 환경에서 Cache 동작을 우회하기 위한 cache repository 구현체.
 * <p>
 * 항상 Empty List를 반환하여 애플리케이션 로직에서 fallback을 유도한다.
 */
@Repository
@Profile("dev")
public class FeedCacheQueryRepositoryNoOpImpl implements FeedCacheQueryRepository {

    @Override
    public List<FeedbackAvailableDowithTaskQueryDto> getFeedbackAvailableDowithTasks() {
        return List.of();
    }
}
