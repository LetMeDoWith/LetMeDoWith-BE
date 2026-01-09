package com.LetMeDoWith.LetMeDoWith.domain.feed.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.feed.model.FeedDowithTask;
import java.util.Comparator;
import java.util.List;

@DomainService
public class LazyDowithTaskSelector {

    public List<FeedDowithTask> selectLazyDowithTasks(List<FeedDowithTask> dowithTasks,
        Integer size) {
        return dowithTasks.stream()
            .sorted(Comparator.comparing(FeedDowithTask::getStartDateTime)
                .thenComparing(FeedDowithTask::getFeedbackCount, Comparator.reverseOrder()))
            .limit(size)
            .toList();
    }
}