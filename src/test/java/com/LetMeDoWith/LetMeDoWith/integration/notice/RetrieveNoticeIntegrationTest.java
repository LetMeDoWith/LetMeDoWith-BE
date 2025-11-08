package com.LetMeDoWith.LetMeDoWith.integration.notice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.domain.notice.model.Notice;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.persistence.jpaRepository.NoticeJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.notice.dto.RetrieveNoticesResDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RetrieveNoticeIntegrationTest extends AbstractIntegrationTest {

    static final String TITLE = "title";
    static final String CONTENT = "content";
    private static final LocalDateTime FIXED_CLOCK_TIME = LocalDateTime.of(2024, 3, 1, 10, 0);
    private static final String RETRIEVE_API_URL = "/api/v1/notices";

    @Autowired
    NoticeJpaRepository noticeJpaRepository;

    @Override
    protected void deleteTestData() {
        noticeJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        setFixedClock(FIXED_CLOCK_TIME);

        Notice notice1 = Notice.of(
                NoticeType.NOTICE,
                TITLE,
                CONTENT,
                LocalDateTime.of(2024, 3, 2, 0, 0),
                LocalDateTime.of(2024, 3, 3, 0, 0),
                "");

        Notice notice2 = Notice.of(
                NoticeType.NOTICE,
                TITLE,
                CONTENT,
                LocalDateTime.of(2024, 3, 2, 0, 0),
                LocalDateTime.of(2024, 3, 3, 0, 0),
                "");

        Notice notice3 = Notice.of(
                NoticeType.NOTICE,
                TITLE,
                CONTENT,
                LocalDateTime.of(2024, 3, 2, 0, 0),
                LocalDateTime.of(2024, 3, 3, 0, 0),
                "");

        noticeJpaRepository.saveAll(List.of(notice1, notice2, notice3));
    }

    @Test
    @DisplayName("[SUCCESS] 성공 - 목록 조회")
    void retrieveNoticesTest() throws Exception {
        int PAGE = 0;
        int SIZE = 2;
        NoticeType notice = NoticeType.NOTICE;

        String URL = RETRIEVE_API_URL + "?type=" + notice.getCode() + "&page=" + PAGE + "&size=" + SIZE;

        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andReturn();

        String content = retrieveResult.getResponse().getContentAsString();

        RetrieveNoticesResDto resDto = this.readPagingResponse(content, RetrieveNoticesResDto.class);

        assertEquals(SIZE, resDto.notices().size());
    }
}
