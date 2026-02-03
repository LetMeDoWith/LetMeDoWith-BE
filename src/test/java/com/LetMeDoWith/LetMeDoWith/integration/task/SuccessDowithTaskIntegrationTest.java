package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskLikeJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveSuccessDowithTasksRes;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.SuccessDowithTaskReqDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class SuccessDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String RETRIEVE_TASKS_URL = "/api/v1/tasks";
    static final String RETRIEVE_SUCCESS_DOWITH_TASKS_URL = "/api/v1/tasks/dowith/success";
    private static final String SUCCESS_TASK_URL = "/api/v1/tasks/dowith/{id}/success";
    private static final String GET_CONFIRM_UPLOAD_PRESIGNED_URL =
            "/api/v1/tasks/dowith/{id}/success/image/upload-presigned-url";

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    private TaskCategoryJpaRepository taskCategoryJpaRepository;

    @Autowired
    private DowithTaskLikeJpaRepository dowithTaskLikeJpaRepository;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.region}")
    private String region;

    private DowithTask waitingDowithTask;
    private TaskCategory taskCategory;

    private List<DowithTask> successDowithTasks = new ArrayList<>();

    private Member member1;
    private Member member2;

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
        waitingDowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "test",
                LocalDate.of(2024, 3, 1),
                LocalTime.of(14, 0)));

        this.member1 = this.memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("test1")
                .selfDescription("test description1")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1995, 11, 4))
                .type(MemberType.USER)
                .build());

        this.member2 = this.memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("test2")
                .selfDescription("test description2")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1995, 11, 5))
                .type(MemberType.USER)
                .build());

        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));

        for (int i = 1; i <= 30; i++) {
            DowithTask dowithTask = DowithTask.of(
                    this.requestMember.getId(),
                    null,
                    "Test Dowith Task " + i,
                    LocalDate.of(2024, 3, i),
                    LocalTime.of(12, 0));
            dowithTask.success(List.of("http://example.com/success_image_" + i + ".jpg"));
            successDowithTasks.add(dowithTask);
        }
        successDowithTasks = dowithTaskJpaRepository.saveAll(successDowithTasks);

        successDowithTasks.sort(Comparator.comparing(DowithTask::getId).reversed());
        // 제일 최신 등록된 DowtithTask는 member1과 member2가 좋아요 누름
        dowithTaskLikeJpaRepository.saveAll(List.of(
                DowithTaskLike.of(this.member1.getId(), successDowithTasks.get(0)),
                DowithTaskLike.of(this.member2.getId(), successDowithTasks.get(0))));

        // 최신에서 두번째로 등록된 DowtithTask는 requestMember와 member1가 좋아요 누름
        dowithTaskLikeJpaRepository.saveAll(List.of(
                DowithTaskLike.of(this.requestMember.getId(), successDowithTasks.get(1)),
                DowithTaskLike.of(this.member1.getId(), successDowithTasks.get(1))));
    }

    @Test
    @DisplayName("[SUCCESS] 두윗모드 Task 인증 사진 업로드 Presigned URL 발급")
    void generateSuccessUploadPresignedUrl1() throws Exception {
        // given
        List<String> confirmImageFileNames = List.of("photo1.jpg", "photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 12, 0));

        // when
        GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto requestBody =
                new GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto(confirmImageFileNames);

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GET_CONFIRM_UPLOAD_PRESIGNED_URL, waitingDowithTask.getId())
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
            resultActions.andExpect(jsonPath("$.data.publicImageUrls[" + i + "]")
                    .value(Matchers.containsString(confirmImageFileNames.get(i))));
        }
    }

    @Test
    @DisplayName("[FAIL] 허용되지 않는 이미지인 경우")
    void generateConfirmUploadPresignedUrl2() throws Exception {
        // given
        List<String> confirmImageFileNames = List.of("photo1.pdf", "photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 12, 0));
        // when
        GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto requestBody =
                new GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto(confirmImageFileNames);

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GET_CONFIRM_UPLOAD_PRESIGNED_URL, waitingDowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[FAIL] 시작 시간 이후에 인증 시도하는 경우")
    void generateSuccessUploadPresignedUrl3() throws Exception {
        // given
        List<String> confirmImageFileNames = List.of("photo1.jpg", "photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 15, 0));
        // when
        GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto requestBody =
                new GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto(confirmImageFileNames);

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GET_CONFIRM_UPLOAD_PRESIGNED_URL, waitingDowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[SUCCESS] Dowith Task 인증 성공")
    void successDowithTask1() throws Exception {
        // given
        List<String> publicImageUrls = List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 10, 0));

        // when
        SuccessDowithTaskReqDto requestBody = new SuccessDowithTaskReqDto(publicImageUrls);
        ResultActions confirmResultActions =
                this.request(MockMvcRequestBuilders.post(SUCCESS_TASK_URL, waitingDowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));
        ResultActions retrieveResultActions = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk());

        // then
        confirmResultActions.andExpect(status().isOk());
        retrieveResultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dowithTasks[0].id").value(waitingDowithTask.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[0].status").value(DowithTaskStatus.SUCCESS.code))
                .andExpect(jsonPath("$.data.dowithTasks[0].confirmedImageUrls").isArray())
                .andExpect(jsonPath("$.data.dowithTasks[0].confirmedImageUrls").value(Matchers.is(publicImageUrls)));
    }

    @Test
    @DisplayName("[FAIL] 시작시간이 지난 후 인증 시도하는 경우")
    void successDowithTask2() throws Exception {
        // given
        List<String> publicImageUrls = List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg");
        this.setFixedClock(LocalDateTime.of(2024, 3, 1, 14, 1));

        // when
        SuccessDowithTaskReqDto requestBody = new SuccessDowithTaskReqDto(publicImageUrls);
        ResultActions confirmResultActions =
                this.request(MockMvcRequestBuilders.post(SUCCESS_TASK_URL, waitingDowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));
        ResultActions retrieveResultActions = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk());

        // then
        confirmResultActions.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[SUCCESS] 성공한 Dowith Task 목록 조회")
    void retrieveSuccessDowithTasks() throws Exception {
        // Given
        int page = 0;
        int size = 10;

        // When
        var resultActions = this.request(get(RETRIEVE_SUCCESS_DOWITH_TASKS_URL)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(this.successDowithTasks.size()));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        RetrieveSuccessDowithTasksRes retrieveSuccessDowithTasksRes =
                this.readPagingResponse(responseBody, RetrieveSuccessDowithTasksRes.class);

        for (int i = 0; i < 10; i++) {
            RetrieveSuccessDowithTasksRes.SuccessDowithTask task =
                    retrieveSuccessDowithTasksRes.successDowithTasks().get(i);

            assertThat(task.id()).isEqualTo(this.successDowithTasks.get(i).getId());
            assertThat(task.title()).isEqualTo(this.successDowithTasks.get(i).getTitle());
            assertThat(task.nickname()).isEqualTo(this.requestMember.getNickname());
            assertThat(task.profileImageUrl()).isEqualTo(this.requestMember.getProfileImageUrl());
            assertThat(task.successImageUrl())
                    .isEqualTo(
                            this.successDowithTasks.get(i).getSuccesses().get(0).getImageUrl());

            if (i == 0) {

                assertThat(task.isLiked()).isFalse();
                assertThat(task.likeCount()).isEqualTo(2L);

            } else if (i == 1) {

                assertThat(task.isLiked()).isTrue();
                assertThat(task.likeCount()).isEqualTo(2L);

            } else {

                assertThat(task.isLiked()).isFalse();
                assertThat(task.likeCount()).isEqualTo(0L);
            }
        }
    }
}
