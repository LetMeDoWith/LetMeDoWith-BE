package com.LetMeDoWith.LetMeDoWith.application.feed.service;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveDowithTaskSuccessImagesResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.repository.FeedDowithTaskQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedTaskService {

    private final FeedDowithTaskQueryRepository feedDowithTaskQueryRepository;

    public RetrieveDowithTaskSuccessImagesResult retrieveDowithTaskSuccessImages(Pageable pageable) {

    }
}
