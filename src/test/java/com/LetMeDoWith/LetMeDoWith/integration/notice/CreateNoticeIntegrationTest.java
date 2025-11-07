package com.LetMeDoWith.LetMeDoWith.integration.notice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.domain.notice.model.Notice;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.persistence.jpaRepository.NoticeJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.notice.dto.CreateNoticeReqDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CreateNoticeIntegrationTest extends AbstractIntegrationTest {

    static final String CREATE_NOTICE_URL = "/api/v1/notices";

    @Autowired
    NoticeJpaRepository noticeJpaRepository;

    @Override
    protected void deleteTestData() {
        noticeJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {}

    @Test
    @DisplayName("[SUCCESS] 성공 - 공지사항/이벤트 생성")
    void createNewNotice() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2025, 11, 1, 0, 0));

        String TITLE = "title";
        String CONTENT = "content";

        CreateNoticeReqDto reqDto = new CreateNoticeReqDto(
                NoticeType.NOTICE,
                TITLE,
                CONTENT,
                LocalDateTime.of(2025, 11, 1, 0, 0),
                LocalDateTime.of(2025, 11, 2, 0, 0),
                "");

        // when
        ResultActions resultActions = this.request(
                MockMvcRequestBuilders.post(CREATE_NOTICE_URL).content(this.writeRequestBodyAsString(reqDto)));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Notice notice = noticeJpaRepository.findAll().get(0);

        assertEquals(NoticeType.NOTICE, notice.getNoticeType());
        assertEquals(TITLE, notice.getTitle());
        assertEquals(CONTENT, notice.getContent());
    }

    @Test
    @DisplayName("[FAIL] 실패 - 게시 날짜 검증 오류")
    void createNewNotice_invalidateStartDateEndDate() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2025, 11, 1, 0, 0));

        String TITLE = "title";
        String CONTENT = "content";

        // case 1: 게시 종료 시점이 시작시작보다 앞선 경우
        CreateNoticeReqDto reqDtoEndDateIsBeforeStartDate = new CreateNoticeReqDto(
                NoticeType.NOTICE,
                TITLE,
                CONTENT,
                LocalDateTime.of(2025, 11, 2, 0, 0),
                LocalDateTime.of(2025, 11, 1, 0, 0),
                "");

        // case 2: 게시 종료 시점이 시작시작보다 앞선 경우
        CreateNoticeReqDto reqDtoStartDateIsBeforeNowDate = new CreateNoticeReqDto(
                NoticeType.NOTICE,
                TITLE,
                CONTENT,
                LocalDateTime.of(2025, 10, 1, 0, 0),
                LocalDateTime.of(2025, 11, 1, 0, 0),
                "");

        // when
        ResultActions resultActionsCase1 = this.request(MockMvcRequestBuilders.post(CREATE_NOTICE_URL)
                .content(this.writeRequestBodyAsString(reqDtoEndDateIsBeforeStartDate)));
        ResultActions resultActionsCase2 = this.request(MockMvcRequestBuilders.post(CREATE_NOTICE_URL)
                .content(this.writeRequestBodyAsString(reqDtoStartDateIsBeforeNowDate)));

        // then
        resultActionsCase1.andExpect(MockMvcResultMatchers.status().isBadRequest());
        resultActionsCase2.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("[FAIL] 실패 - 게시 컨텐츠 검증 오류")
    void createNewNotice_invalidTitleOrContent() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2025, 11, 1, 0, 0));

        String TITLE = "";
        String CONTENT = "   ";

        CreateNoticeReqDto reqDto = new CreateNoticeReqDto(
                NoticeType.NOTICE,
                TITLE,
                CONTENT,
                LocalDateTime.of(2025, 11, 1, 0, 0),
                LocalDateTime.of(2025, 11, 2, 0, 0),
                "");

        // when
        ResultActions resultActions = this.request(
                MockMvcRequestBuilders.post(CREATE_NOTICE_URL).content(this.writeRequestBodyAsString(reqDto)));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
