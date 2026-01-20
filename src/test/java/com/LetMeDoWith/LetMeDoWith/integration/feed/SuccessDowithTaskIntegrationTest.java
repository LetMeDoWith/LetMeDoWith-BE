package com.LetMeDoWith.LetMeDoWith.integration.feed;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class SuccessDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String RETRIVE_SUCCESS_DOWITH_TASKS = "/api/v1/feeds/tasks/dowith/success";

    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;

    private List<DowithTask> dowithTasks = new ArrayList<>();

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalCount").value(this.dowithTasks.size()));
    }
}
