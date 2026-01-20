package com.LetMeDoWith.LetMeDoWith.application.feed.repository;

import com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto.SuccessDowithTaskQueryDto;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FeedDowithTaskQueryRepository {
    Long countSuccessDowithTasks();

    List<SuccessDowithTaskQueryDto> getSuccessDowithTasks(String requestMemberId, int offset, int limit);

    Map<Long, Long> countDowithTaskLikes(Set<Long> dowithTaskIds);
}
