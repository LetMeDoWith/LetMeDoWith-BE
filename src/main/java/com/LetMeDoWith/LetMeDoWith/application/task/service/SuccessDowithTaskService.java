package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.client.FileClient;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CancelLikeSuccessDowithTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.LikeSuccessDowithTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveSuccessDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskLikeRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.SuccessDowithTaskQueryDto;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SuccessDowithTaskService {

    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskQueryRepository dowithTaskQueryRepository;
    private final DowithTaskLikeRepository dowithTaskLikeRepository;
    private final FileClient fileClient;

    @Lazy
    @Autowired
    private SuccessDowithTaskService self;

    public List<String> generateDowithTaskSuccessImageUploadPresignedUrls(
            String memberId, Long dowithTaskId, List<String> imageFileNames) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        List<String> keys = dowithTask.generateSuccessImageKey(imageFileNames);
        return keys.stream()
                .map(key -> fileClient.getUploadPresignedUrl(key, Duration.ofSeconds(30)))
                .toList();
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
    public LikeSuccessDowithTaskResult likeSuccessDowithTask(String memberId, Long dowithTaskId) {
        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        boolean isAlreadyLiked = false;
        try {
            // insert 시도하는 부분만 트랜잭션 분리
            // 분리하지 않을 경우, DowithTaskLike entity의 id가 null로 남아있어서 트랜잭션 커밋 시점에 UnexpectedRollbackException 발생
            self.tryInsertLikeInNewTransaction(dowithTask, memberId);
        } catch (DataIntegrityViolationException e) {
            isAlreadyLiked = true;
        }

        long likeCount = dowithTaskLikeRepository.countDowithTaskLikesByDowithTaskId(dowithTaskId);
        return new LikeSuccessDowithTaskResult(isAlreadyLiked, likeCount);
    }

    @Transactional
    public void tryInsertLikeInNewTransaction(DowithTask dowithTask, String memberId) {
        DowithTaskLike like = dowithTask.like(memberId);
        dowithTaskLikeRepository.save(like);
    }

    @Transactional
    public CancelLikeSuccessDowithTaskResult cancelLikeSuccessDowithTask(String memberId, Long dowithTaskId) {
        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        Optional<DowithTaskLike> opDowithTaskLike = dowithTaskLikeRepository.getDowithTaskLike(memberId, dowithTask);

        boolean isAlreadyCanceled = false;
        if (opDowithTaskLike.isEmpty()) {
            isAlreadyCanceled = true;
        } else {
            boolean deleted = dowithTaskLikeRepository.delete(opDowithTaskLike.get());
            isAlreadyCanceled = !deleted;
        }

        long likeCount = dowithTaskLikeRepository.countDowithTaskLikesByDowithTaskId(dowithTaskId);

        return new CancelLikeSuccessDowithTaskResult(isAlreadyCanceled, likeCount);
    }
}
