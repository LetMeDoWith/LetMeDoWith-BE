package com.LetMeDoWith.LetMeDoWith.domain.feed.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.feed.model.FeedDowithTask;
import java.util.Comparator;
import java.util.List;

@DomainService
public class LazyDowithTaskSelector {

    public List<FeedDowithTask> selectLazyDowithTasks(List<FeedDowithTask> dowithTasks) {
        final int LAZY_COUNT = 5;

        return dowithTasks.stream()
                .sorted(Comparator.comparing(FeedDowithTask::startDateTime)
                        .thenComparing(FeedDowithTask::feedbackCount, Comparator.reverseOrder()))
                .limit(LAZY_COUNT)
                .toList();
    }
}
