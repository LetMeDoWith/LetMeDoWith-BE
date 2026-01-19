package com.LetMeDoWith.LetMeDoWith.application.feed.repository;

import com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto.DowithTaskSuccessImageQueryDto;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FeedDowithTaskQueryRepository {
    Long countDowithTaskSuccessImages();

    List<DowithTaskSuccessImageQueryDto> getDowithTaskSuccessImages(String requestMemberId, int offset, int limit);

    Map<Long, Long> countDowithTaskLikes(Set<Long> dowithTaskIds);
}
