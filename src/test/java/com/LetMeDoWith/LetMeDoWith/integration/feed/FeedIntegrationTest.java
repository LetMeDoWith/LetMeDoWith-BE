package com.LetMeDoWith.LetMeDoWith.integration.feed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicy;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.redis.RedisOperator;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.feed.dto.RetrieveFeedbackAvailableDowithTasksResDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class FeedIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();

        // Redis 데이터 초기화
        deleteRedisKey(CachePolicy.DOWITH_TASK_IDS.cacheName());
        deleteRedisKey(CachePolicy.DOWITH_TASK.cacheName());
    }

    private void deleteRedisKey(String keyPattern) {
        Set<String> keys = redisTemplate.keys(keyPattern + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    protected void createTestData() {
        // 개별 테스트 메서드에서 필요한 데이터를 생성합니다.
    }

    @Test
    @DisplayName("Redis에 데이터가 존재할 경우 Redis 데이터를 반환한다 (Cache Hit)")
    void should_return_data_from_redis_when_exists() throws Exception {

        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 2, 0, 0, 0);
        setFixedClock(testDateTime);

        // Given
        Long taskId = 1L;
        String title = "Redis Task Title";
        LocalDateTime requestDateTime = SystemTimeUtil.now().minusMinutes(30);
        FeedbackAvailableDowithTaskQueryDto dto = new FeedbackAvailableDowithTaskQueryDto(
                taskId,
                "test_member_id",
                "nickname",
                "badge_url",
                title,
                "WAIT",
                requestDateTime.toLocalDate(),
                requestDateTime.toLocalTime(),
                0L);

        // Redis 적재 - List (ID 목록)
        redisOperator.pushRightAll(CachePolicy.DOWITH_TASK_IDS, "", List.of(String.valueOf(taskId)));

        // Redis 적재 - Hash (객체 정보)
        redisOperator.putHash(CachePolicy.DOWITH_TASK, String.valueOf(taskId), dto);

        // When
        ResultActions resultActions = request(MockMvcRequestBuilders.get("/api/v1/feeds/tasks/dowith"));

        // Then
        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        RetrieveFeedbackAvailableDowithTasksResDto response = readResponse(
                mvcResult.getResponse().getContentAsString(), RetrieveFeedbackAvailableDowithTasksResDto.class);

        assertThat(response.dowithTasks()).hasSize(1);
        assertThat(response.dowithTasks().get(0).id()).isEqualTo(taskId);
        assertThat(response.dowithTasks().get(0).title()).isEqualTo(title);
    }

    @Test
    @DisplayName("Redis에 데이터가 없을 경우 DB에서 조건에 맞는 데이터를 조회한다 (DB Fallback)")
    void should_fallback_to_db_when_redis_is_empty() throws Exception {
        // --- [Phase 1: 데이터 준비] ---
        // 1. 시계를 '데이터 생성 시점'으로 설정 (테스트 기준 시간 5시간 전)
        //    DowithTask.validateStartDateTime 검증(생성 시 시작시간은 미래여야 함)을 통과하기 위함
        LocalDateTime testReferenceTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0); // 조회 시점 (12:00)
        LocalDateTime dataCreationTime = testReferenceTime.minusHours(5); // 생성 시점 (07:00)
        setFixedClock(dataCreationTime);

        // 2. 데이터 생성 (07:00 시점 기준)
        // Target Task: 시작 시간 11:30 (현재 07:00 기준 미래 -> 생성 가능)
        DowithTask targetTask = DowithTask.of(
                requestMember.getId(),
                1L,
                "Target Task",
                testReferenceTime.toLocalDate(),
                testReferenceTime.toLocalTime().minusMinutes(30) // 11:30
                );

        // Expired Task: 시작 시간 10:00 (현재 07:00 기준 미래 -> 생성 가능)
        DowithTask expiredTask = DowithTask.of(
                requestMember.getId(),
                1L,
                "Expired Task",
                testReferenceTime.toLocalDate(),
                testReferenceTime.toLocalTime().minusHours(2) // 10:00
                );

        // Future Task: 시작 시간 12:10 (현재 07:00 기준 미래 -> 생성 가능)
        DowithTask futureTask = DowithTask.of(
                requestMember.getId(),
                1L,
                "Future Task",
                testReferenceTime.toLocalDate(),
                testReferenceTime.toLocalTime().plusMinutes(10) // 12:10
                );

        dowithTaskJpaRepository.saveAll(List.of(targetTask, expiredTask, futureTask));

        // --- [Phase 2: 테스트 수행] ---
        // 3. 시계를 '테스트 기준 시간(12:00)'으로 변경 (시간 이동!)
        //    이제 11:30 Task는 과거(30분 전) 데이터가 됨 -> 조회 대상
        //    10:00 Task는 과거(2시간 전) 데이터가 됨 -> 만료됨
        //    12:10 Task는 여전히 미래 -> 아직 시작 안함
        setFixedClock(testReferenceTime);

        // When
        ResultActions resultActions = request(MockMvcRequestBuilders.get("/api/v1/feeds/tasks/dowith"));

        // Then
        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        RetrieveFeedbackAvailableDowithTasksResDto response = readResponse(
                mvcResult.getResponse().getContentAsString(), RetrieveFeedbackAvailableDowithTasksResDto.class);

        assertThat(response.dowithTasks()).hasSize(1);
        assertThat(response.dowithTasks().get(0).title()).isEqualTo("Target Task");
        assertThat(response.dowithTasks().get(0).status()).isEqualTo("WAIT");
    }
}
