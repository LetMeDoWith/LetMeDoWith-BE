package com.LetMeDoWith.LetMeDoWith.integration.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.RetrieveMyDowithResDto;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MyDowithIntegrationTest extends AbstractIntegrationTest {

    static final String MY_DOWITH_URL = "/api/v1/members/me/my-dowith";
    static final String MEMBER_DOWITH_URL = "/api/v1/members/%s/my-dowith";

    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        SystemTimeUtil.setClock(
                Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

        DowithTask successTask1 =
                DowithTask.of(requestMember.getId(), null, "성공 두윗 1", LocalDate.of(2024, 3, 10), LocalTime.of(10, 0));
        successTask1.success(List.of("https://example.com/image1.jpg"));

        DowithTask successTask2 =
                DowithTask.of(requestMember.getId(), null, "성공 두윗 2", LocalDate.of(2024, 3, 11), LocalTime.of(11, 0));
        successTask2.success(List.of("https://example.com/image2.jpg"));

        DowithTask waitTask =
                DowithTask.of(requestMember.getId(), null, "대기 두윗", LocalDate.of(2024, 3, 12), LocalTime.of(12, 0));

        dowithTaskJpaRepository.saveAll(List.of(successTask1, successTask2, waitTask));
    }

    @Test
    @DisplayName("[SUCCESS] 내 마이두윗 정보 조회 - 기본 정보와 성공한 두윗 갯수를 반환한다.")
    void retrieveMyDowithInfo() throws Exception {
        // when
        MvcResult result = this.request(MockMvcRequestBuilders.get(MY_DOWITH_URL))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveMyDowithResDto resDto =
                this.readResponse(result.getResponse().getContentAsString(), RetrieveMyDowithResDto.class);

        // then
        assertEquals(requestMember.getId(), resDto.memberId());
        assertEquals(requestMember.getNickname(), resDto.nickname());
        assertEquals(requestMember.getSelfDescription(), resDto.selfDescription());
        assertEquals(2L, resDto.successDowithCount());
    }

    @Test
    @DisplayName("[SUCCESS] 특정 멤버의 마이두윗 정보 조회 - 기본 정보와 성공한 두윗 갯수를 반환한다.")
    void retrieveMemberDowithInfo() throws Exception {
        // when
        MvcResult result = this.request(MockMvcRequestBuilders.get(MEMBER_DOWITH_URL.formatted(requestMember.getId())))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveMyDowithResDto resDto =
                this.readResponse(result.getResponse().getContentAsString(), RetrieveMyDowithResDto.class);

        // then
        assertEquals(requestMember.getId(), resDto.memberId());
        assertEquals(requestMember.getNickname(), resDto.nickname());
        assertEquals(requestMember.getSelfDescription(), resDto.selfDescription());
        assertEquals(2L, resDto.successDowithCount());
    }

    @Test
    @DisplayName("[SUCCESS] 성공한 두윗이 없는 경우 - successDowithCount 0을 반환한다.")
    void retrieveMyDowithInfoWithNoSuccessTasks() throws Exception {
        // given
        dowithTaskJpaRepository.deleteAll();

        // when
        MvcResult result = this.request(MockMvcRequestBuilders.get(MY_DOWITH_URL))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveMyDowithResDto resDto =
                this.readResponse(result.getResponse().getContentAsString(), RetrieveMyDowithResDto.class);

        // then
        assertEquals(requestMember.getId(), resDto.memberId());
        assertEquals(0L, resDto.successDowithCount());
    }

    @Test
    @DisplayName("[FAIL] 특정 멤버의 마이두윗 정보 조회 - 존재하지 않는 memberId로 요청 시 실패한다.")
    void retrieveMemberDowithInfo_notFound() throws Exception {
        // given
        String nonExistentMemberId = "NON_EXISTENT_MEMBER_ID";

        // when
        this.request(MockMvcRequestBuilders.get(MEMBER_DOWITH_URL.formatted(nonExistentMemberId)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
