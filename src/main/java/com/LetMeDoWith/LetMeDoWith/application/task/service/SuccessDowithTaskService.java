package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationSendService;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CancelLikeSuccessDowithTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.LikeSuccessDowithTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveDowithTaskLikersResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveSuccessDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.common.dto.GenerateUploadPresignedUrlsResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.FileNamespace;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.service.PresignedUrlService;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskLikeRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.SuccessDowithTaskQueryDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SuccessDowithTaskService {

    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskQueryRepository dowithTaskQueryRepository;
    private final DowithTaskLikeRepository dowithTaskLikeRepository;
    private final PresignedUrlService presignedUrlService;
    private final NotificationSendService notificationSendService;

    @Lazy
    @Autowired
    private SuccessDowithTaskService self;

    public GenerateUploadPresignedUrlsResult generateDowithTaskSuccessImageUploadPresignedUrls(
            String memberId, Long dowithTaskId, List<String> imageFileNames) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        dowithTask.validateSuccessImageUploadAvailable();

        return presignedUrlService.generateUploadPresignedUrls(FileNamespace.DOWITH_TASK_CONFIRM, imageFileNames);
    }

    @Transactional
    public void successDowithTask(String memberId, Long dowithTaskId, List<String> imageUrls) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        dowithTask.success(imageUrls);
    }

    /**
     * DowithTask 성공 이미지 피드 조회
     *
     * @param requestMemberId
     * @param pageable
     * @return
     */
    public RetrieveSuccessDowithTasksResult retrieveSuccessDowithTasks(String requestMemberId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        //        long totalCount = dowithTaskQueryRepository.countSuccessDowithTasks();
        long totalCount = dowithTaskRepository.countByStatus(DowithTaskStatus.SUCCESS);
        List<SuccessDowithTaskQueryDto> successImages =
                dowithTaskQueryRepository.getSuccessDowithTasks(requestMemberId, offset, limit);

        Map<Long, Long> dowithTaskLikeCountMap = dowithTaskQueryRepository.countDowithTaskLikes(
                successImages.stream().map(SuccessDowithTaskQueryDto::id).collect(Collectors.toSet()));

        return RetrieveSuccessDowithTasksResult.of(totalCount, successImages, dowithTaskLikeCountMap);
    }

    /**
     * 성공 DowithTask 좋아요
     *
     * @param memberId
     * @param dowithTaskId
     * @return
     */
    @Transactional
    public LikeSuccessDowithTaskResult likeSuccessDowithTask(String memberId, Long dowithTaskId) {
        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        boolean isAlreadyLiked = false;

        DowithTaskLike like = dowithTask.like(memberId);
        int savedRowCount = dowithTaskLikeRepository.saveIgnore(like);
        isAlreadyLiked = savedRowCount == 0; // 저장된 행이 없으면 이미 좋아요가 존재하는 상태

        long likeCount = dowithTaskLikeRepository.countDowithTaskLikesByDowithTaskId(dowithTaskId);

        if (!isAlreadyLiked) {
            notificationSendService.sendNotification(memberId, dowithTask.getMemberId(), "LIKE_RECEIVED", true);
        }

        return new LikeSuccessDowithTaskResult(isAlreadyLiked, likeCount);
    }

    /**
     * 성공 DowithTask 좋아요 수 조회
     *
     * @param dowithTaskId 두윗 Task ID
     */
    public long retrieveSuccessDowithTaskLikeCount(Long dowithTaskId) {
        dowithTaskRepository
                .getDowithTask(dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        return dowithTaskLikeRepository.countDowithTaskLikesByDowithTaskId(dowithTaskId);
    }

    public RetrieveDowithTaskLikersResult retrieveDowithTaskLikers(Long dowithTaskId, Pageable pageable) {
        dowithTaskRepository
                .getDowithTask(dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        long totalCount = dowithTaskQueryRepository.countDowithTaskLikes(dowithTaskId);
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        return RetrieveDowithTaskLikersResult.of(
                totalCount, dowithTaskQueryRepository.getDowithTaskLikers(dowithTaskId, offset, limit));
    }

    @Transactional
    public CancelLikeSuccessDowithTaskResult cancelLikeSuccessDowithTask(String memberId, Long dowithTaskId) {
        dowithTaskRepository
                .getDowithTask(dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        long deletedRowCount = dowithTaskLikeRepository.delete(dowithTaskId, memberId);
        boolean isAlreadyCanceled = deletedRowCount == 0; // 삭제된 행이 없으면 이미 취소된 상태

        long likeCount = dowithTaskLikeRepository.countDowithTaskLikesByDowithTaskId(dowithTaskId);

        return new CancelLikeSuccessDowithTaskResult(isAlreadyCanceled, likeCount);
    }
}
