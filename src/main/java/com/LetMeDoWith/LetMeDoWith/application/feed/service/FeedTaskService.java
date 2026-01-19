package com.LetMeDoWith.LetMeDoWith.application.feed.service;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveDowithTaskSuccessImagesResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.repository.FeedDowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto.DowithTaskSuccessImageQueryDto;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedTaskService {

    private final FeedDowithTaskQueryRepository feedDowithTaskQueryRepository;

    /**
     * DowithTask 성공 이미지 피드 조회
     *
     * @param requestMemberId
     * @param pageable
     * @return
     */
    public RetrieveDowithTaskSuccessImagesResult retrieveDowithTaskSuccessImages(
            String requestMemberId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        var totalCount = feedDowithTaskQueryRepository.countDowithTaskSuccessImages();
        var successImages = feedDowithTaskQueryRepository.getDowithTaskSuccessImages(requestMemberId, offset, limit);

        Map<Long, Long> dowithTaskLikeCountMap =
                feedDowithTaskQueryRepository.countDowithTaskLikes(successImages.stream()
                        .map(DowithTaskSuccessImageQueryDto::dowithTaskId)
                        .collect(Collectors.toSet()));

        return RetrieveDowithTaskSuccessImagesResult.of(totalCount, successImages, dowithTaskLikeCountMap);
    }
}
