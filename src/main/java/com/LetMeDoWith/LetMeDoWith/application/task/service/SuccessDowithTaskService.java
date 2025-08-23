package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.client.FileClient;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuccessDowithTaskService {

    private final DowithTaskRepository dowithTaskRepository;
    private final FileClient fileClient;

    public List<String> generateDowithTaskConfirmImageUploadPresignedUrls(
            String memberId, Long dowithTaskId, List<String> imageFileNames) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        List<String> keys = dowithTask.generateConfirmImageKey(imageFileNames);
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
}
