package com.LetMeDoWith.LetMeDoWith.integration.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.StoreFeedbackAvailableDowithTasksTasklet;
import com.LetMeDoWith.LetMeDoWith.common.redis.StorePolicy;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.FeedQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.redis.RedisOperator;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBatchTest
class StoreFeedbackAvailableDowithTasksTaskletIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StoreFeedbackAvailableDowithTasksTasklet tasklet;

    @MockBean
    private RedisOperator redisOperator;

    @MockBean
    private FeedQueryRepository feedQueryRepository;

    @Override
    protected void createTestData() {
        // MockBean 사용하므로 데이터 생성 불필요
    }

    @Override
    protected void deleteTestData() {
        // MockBean 사용하므로 데이터 정리 불필요
    }

    @Test
    @DisplayName("Tasklet 실행 시 RedisOperator가 정상적으로 호출되는지 검증한다.")
    void testExecute_Integration() throws Exception {
        // Given
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        ReflectionTestUtils.setField(tasklet, "executionDateTime", fixedTime);

        FeedbackAvailableDowithTaskQueryDto dto1 = new FeedbackAvailableDowithTaskQueryDto(
                1L,
                "user1",
                "nick1",
                "http://img1",
                "Title1",
                "WAITING",
                LocalDate.of(2025, 1, 1),
                LocalTime.of(9, 0),
                0L);
        FeedbackAvailableDowithTaskQueryDto dto2 = new FeedbackAvailableDowithTaskQueryDto(
                2L,
                "user2",
                "nick2",
                "http://img2",
                "Title2",
                "WAITING",
                LocalDate.of(2025, 1, 1),
                LocalTime.of(10, 0),
                1L);
        List<FeedbackAvailableDowithTaskQueryDto> tasks = List.of(dto1, dto2);

        given(feedQueryRepository.getFeedbackAvailableDowithTasks(any())).willReturn(tasks);

        // When
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        RepeatStatus status = StepScopeTestUtils.doInStepScope(stepExecution, () -> tasklet.execute(null, null));

        // Then
        assertEquals(RepeatStatus.FINISHED, status);

        // RedisOperator 호출 검증 (상호작용 테스트)
        // 1. 상세 정보 저장 호출 확인
        verify(redisOperator).putHashes(eq(StorePolicy.DOWITH_TASK), eq(tasks), any());

        // 2. ID 목록 저장 호출 확인 (Atomic Rename 로직 검증)
        // pushRightAll -> rename 순서로 호출되었는지 확인
        verify(redisOperator).pushRightAll(eq(StorePolicy.DOWITH_TASK_IDS), anyString(), anyList());
        verify(redisOperator).rename(eq(StorePolicy.DOWITH_TASK_IDS), anyString(), eq(""));
    }
}
