package com.LetMeDoWith.LetMeDoWith.integration.task;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.successDowithTaskReqDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SuccessDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String RETRIEVE_TASKS_URL = "/api/v1/tasks";
    private static final String SUCCESS_TASK_URL = "/api/v1/tasks/dowith/{dowithTaskId}/success";
    private static final String GET_CONFIRM_UPLOAD_PRESIGNED_URL =
            "/api/v1/tasks/dowith/{dowithTaskId}/confirm/image/upload-presigned-url";

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    private TaskCategoryJpaRepository taskCategoryJpaRepository;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.region}")
    private String region;

    private DowithTask dowithTask;
    private TaskCategory taskCategory;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
        taskCategoryJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        taskCategory = taskCategoryJpaRepository.save(TaskCategory.of(
                "test", TaskCategory.TaskCategoryCreationType.COMMON, "test", this.requestMember.getId()));
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 13, 0));
        dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "test",
                LocalDate.of(2024, 3, 1),
                LocalTime.of(14, 0)));
    }

    @Test
    @DisplayName("[SUCCESS] 두윗모드 Task 인증 사진 업로드 Presigned URL 발급")
    void generateConfirmUploadPresignedUrl1() throws Exception {
        // given
        List<String> confirmImageFileNames = List.of("photo1.jpg", "photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 12, 0));

        // when
        GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto requestBody =
                new GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto(confirmImageFileNames);

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GET_CONFIRM_UPLOAD_PRESIGNED_URL, dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().isOk());
        String presignedUrlPrefix =
                String.format("https://%s.s3.%s.amazonaws.com/dowith_task_confirms", bucketName, region);
        for (int i = 0; i < confirmImageFileNames.size(); i++) {
            resultActions.andExpect(jsonPath("$.data.presignedUrls[" + i + "]")
                    .value(Matchers.containsString(confirmImageFileNames.get(i))));
            resultActions.andExpect(
                    jsonPath("$.data.presignedUrls[" + i + "]").value(Matchers.containsString(presignedUrlPrefix)));
        }
    }

    @Test
    @DisplayName("[FAIL] 허용되지 않는 이미지인 경우")
    void generateConfirmUploadPresignedUrl2() throws Exception {
        // given
        List<String> confirmImageFileNames = List.of("photo1.pdf", "photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 12, 0));
        // when
        GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto requestBody =
                new GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto(confirmImageFileNames);

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GET_CONFIRM_UPLOAD_PRESIGNED_URL, dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[FAIL] 시작 시간 이후에 인증 시도하는 경우")
    void generateConfirmUploadPresignedUrl3() throws Exception {
        // given
        List<String> confirmImageFileNames = List.of("photo1.jpg", "photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 15, 0));
        // when
        GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto requestBody =
                new GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto(confirmImageFileNames);

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GET_CONFIRM_UPLOAD_PRESIGNED_URL, dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[SUCCESS] Dowith Task 인증 성공")
    void confirmDowithTask1() throws Exception {
        // given
        List<String> publicImageUrls = List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 10, 0));

        // when
        successDowithTaskReqDto requestBody = new successDowithTaskReqDto(publicImageUrls);
        ResultActions confirmResultActions =
                this.request(MockMvcRequestBuilders.post(SUCCESS_TASK_URL, dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));
        ResultActions retrieveResultActions = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk());

        // then
        confirmResultActions.andExpect(status().isOk());
        retrieveResultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dowithTasks[0].id").value(dowithTask.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[0].status").value(DowithTaskStatus.SUCCESS.code))
                .andExpect(jsonPath("$.data.dowithTasks[0].confirmedImageUrls").isArray())
                .andExpect(jsonPath("$.data.dowithTasks[0].confirmedImageUrls").value(Matchers.is(publicImageUrls)));
    }

    @Test
    @DisplayName("[FAIL] 시작시간이 지난 후 인증 시도하는 경우")
    void confirmDowithTask2() throws Exception {
        // given
        List<String> publicImageUrls = List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 14, 1));

        // when
        successDowithTaskReqDto requestBody = new successDowithTaskReqDto(publicImageUrls);
        ResultActions confirmResultActions =
                this.request(MockMvcRequestBuilders.post(SUCCESS_TASK_URL, dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));
        ResultActions retrieveResultActions = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk());

        // then
        confirmResultActions.andExpect(status().is4xxClientError());
    }
}
