package com.LetMeDoWith.LetMeDoWith.application.feed.service;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetreiveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.repository.FeedQueryRepository;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;    
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedQueryRepository feedQueryRepository;

    public RetreiveFeedbackAvailableDowithTasksResult retrieveFeedbackAvailableDowithTasks(Pageable pageable) {
        String memberId = AuthUtil.getMemberId();
        List<FeedDowithTaskQueryDto> dto = feedQueryRepository.getFeedbackAvailableDowithTasks(
                memberId, pageable.getOffset(), pageable.getPageSize());
        return RetreiveFeedbackAvailableDowithTasksResult.from(dto);
    }
}
