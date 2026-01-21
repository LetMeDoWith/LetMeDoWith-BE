package com.LetMeDoWith.LetMeDoWith.domain.feed.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import java.util.Comparator;
import java.util.List;

@DomainService
public class LazyDowithTaskSelector {

    public List<FeedDowithTaskQueryDto> selectLazyDowithTasks(List<FeedDowithTaskQueryDto> dowithTasks) {
        final int LAZY_COUNT = 5;

        return dowithTasks.stream()
                .sorted(Comparator.comparing(FeedDowithTaskQueryDto::startDateTime)
                        .thenComparing(FeedDowithTaskQueryDto::feedbackCount, Comparator.reverseOrder()))
                .limit(LAZY_COUNT)
                .toList();
    }
}
