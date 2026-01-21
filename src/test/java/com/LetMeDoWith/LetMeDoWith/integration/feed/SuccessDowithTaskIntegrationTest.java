package com.LetMeDoWith.LetMeDoWith.integration.feed;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskLike;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskLikeJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SuccessDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String RETRIVE_SUCCESS_DOWITH_TASKS = "/api/v1/feeds/tasks/dowith/success";

    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    DowithTaskLikeJpaRepository dowithTaskLikeJpaRepository;

    private List<DowithTask> dowithTasks = new ArrayList<>();

    private Member member1;
    private Member member2;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {

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
            dowithTasks.add(dowithTask);
        }
        dowithTasks = dowithTaskJpaRepository.saveAll(dowithTasks);

        dowithTasks.sort(Comparator.comparing(DowithTask::getId).reversed());
        // 제일 최신 등록된 DowtithTask는 member1과 member2가 좋아요 누름
        dowithTaskLikeJpaRepository.saveAll(List.of(
                DowithTaskLike.of(this.member1.getId(), dowithTasks.get(0)),
                DowithTaskLike.of(this.member2.getId(), dowithTasks.get(0))));

        // 최신에서 두번째로 등록된 DowtithTask는 requestMember와 member1가 좋아요 누름
        dowithTaskLikeJpaRepository.saveAll(List.of(
                DowithTaskLike.of(this.requestMember.getId(), dowithTasks.get(1)),
                DowithTaskLike.of(this.member1.getId(), dowithTasks.get(1))));
    }

    @Test
    @DisplayName("[SUCCESS] 성공한 Dowith Task 목록 조회")
    void retrieveSuccessDowithTasks() throws Exception {
        // Given
        int page = 0;
        int size = 10;

        // When
        var resultActions = this.request(get(RETRIVE_SUCCESS_DOWITH_TASKS)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(this.dowithTasks.size()));

        for (int i = 0; i < 10; i++) {
            DowithTask task = this.dowithTasks.get(i);

            resultActions
                    .andExpect(jsonPath("$.data.successDowithTasks[%d].id".formatted(i))
                            .value(task.getId()))
                    .andExpect(jsonPath("$.data.successDowithTasks[%d].title".formatted(i))
                            .value(task.getTitle()))
                    .andExpect(jsonPath("$.data.successDowithTasks[%d].nickname".formatted(i))
                            .value(this.requestMember.getNickname()))
                    .andExpect(jsonPath("$.data.successDowithTasks[%d].profileImageUrl".formatted(i))
                            .value(this.requestMember.getProfileImageUrl()))
                    .andExpect(jsonPath("$.data.successDowithTasks[%d].successImageUrl".formatted(i))
                            .value(task.getSuccesses().get(0).getImageUrl()));

            if (i == 0) {
                resultActions
                        .andExpect(jsonPath("$.data.successDowithTasks[%d].isLiked".formatted(i))
                                .value(false))
                        .andExpect(jsonPath("$.data.successDowithTasks[%d].likeCount".formatted(i))
                                .value(2));
            } else if (i == 1) {
                resultActions
                        .andExpect(jsonPath("$.data.successDowithTasks[%d].isLiked".formatted(i))
                                .value(true))
                        .andExpect(jsonPath("$.data.successDowithTasks[%d].likeCount".formatted(i))
                                .value(2));
            } else {
                resultActions
                        .andExpect(jsonPath("$.data.successDowithTasks[%d].isLiked".formatted(i))
                                .value(false))
                        .andExpect(jsonPath("$.data.successDowithTasks[%d].likeCount".formatted(i))
                                .value(0));
            }
        }
    }
}
