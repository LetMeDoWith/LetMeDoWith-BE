package com.LetMeDoWith.LetMeDoWith.integration.ranking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.Ranking;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveMyRankingResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveRankingTopicsResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveRankingsResDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RankingIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/rankings";
    private static final int YEAR = 2026;
    private static final int MONTH = 2;
    private static final int WEEK = 3;

    @PersistenceContext
    private EntityManager entityManager;

    private Long mainTopicId;
    private Long emptyTopicId;

    @Override
    protected void deleteTestData() {
        entityManager.createNativeQuery("DELETE FROM ranking").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM ranking_topic").executeUpdate();
    }

    @Override
    protected void createTestData() {
        RankingTopic mainTopic = RankingTopic.builder()
                .title("이번 주 독서왕")
                .description("한 주 동안 가장 많이 독서한 사용자를 확인해요.")
                .build();
        entityManager.persist(mainTopic);

        RankingTopic emptyTopic = RankingTopic.builder()
                .title("이번 주 운동왕")
                .description("운동 루틴 완료율이 높은 사용자를 확인해요.")
                .build();
        entityManager.persist(emptyTopic);

        this.mainTopicId = mainTopic.getId();
        this.emptyTopicId = emptyTopic.getId();

        List<Member> extraMembers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            extraMembers.add(memberJpaRepository.save(Member.builder()
                    .status(MemberStatus.NORMAL)
                    .type(MemberType.USER)
                    .nickname("ranking-user-" + i)
                    .build()));
        }

        List<Ranking> rankings = new ArrayList<>();
        rankings.add(Ranking.builder()
                .rankingTopic(mainTopic)
                .year(YEAR)
                .month(MONTH)
                .week(WEEK)
                .memberId(requestMember.getId())
                .currentRank(1L)
                .previousRank(2L)
                .build());

        for (int i = 0; i < extraMembers.size(); i++) {
            rankings.add(Ranking.builder()
                    .rankingTopic(mainTopic)
                    .year(YEAR)
                    .month(MONTH)
                    .week(WEEK)
                    .memberId(extraMembers.get(i).getId())
                    .currentRank((long) (i + 2))
                    .previousRank((long) (i + 3))
                    .build());
        }

        rankings.forEach(entityManager::persist);
        entityManager.flush();
    }

    @Test
    @DisplayName("[SUCCESS] 랭킹 토픽 목록 조회")
    void retrieveRankingTopics_success() throws Exception {
        MvcResult result = this.request(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveRankingTopicsResDto response =
                this.readResponse(result.getResponse().getContentAsString(), RetrieveRankingTopicsResDto.class);

        assertEquals(2, response.rankingTopics().size());
    }

    @Test
    @DisplayName("[SUCCESS] 랭킹 상세 조회 - 기본 size(5) 적용")
    void retrieveRankings_defaultSize_success() throws Exception {
        String url = BASE_URL + "/" + mainTopicId + "?year=" + YEAR + "&month=" + MONTH + "&week=" + WEEK;

        MvcResult result = this.request(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveRankingsResDto response =
                this.readResponse(result.getResponse().getContentAsString(), RetrieveRankingsResDto.class);

        assertEquals(5, response.rankings().size());
        assertEquals(1L, response.rankings().get(0).currentRank());
    }

    @Test
    @DisplayName("[SUCCESS] 랭킹 상세 조회 - size 지정")
    void retrieveRankings_withSize_success() throws Exception {
        String url = BASE_URL + "/" + mainTopicId + "?year=" + YEAR + "&month=" + MONTH + "&week=" + WEEK + "&size=3";

        MvcResult result = this.request(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveRankingsResDto response =
                this.readResponse(result.getResponse().getContentAsString(), RetrieveRankingsResDto.class);

        assertEquals(3, response.rankings().size());
    }

    @Test
    @DisplayName("[FAIL] 랭킹 상세 조회 - size가 1 미만이면 INVALID_PARAM_ERROR")
    void retrieveRankings_invalidSize_fail() throws Exception {
        String url = BASE_URL + "/" + mainTopicId + "?year=" + YEAR + "&month=" + MONTH + "&week=" + WEEK + "&size=0";

        MvcResult result = this.request(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("E201"));
    }

    @Test
    @DisplayName("[SUCCESS] 내 랭킹 조회")
    void retrieveMyRanking_success() throws Exception {
        String url = BASE_URL + "/" + mainTopicId + "/me?year=" + YEAR + "&month=" + MONTH + "&week=" + WEEK;

        MvcResult result = this.request(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveMyRankingResDto response =
                this.readResponse(result.getResponse().getContentAsString(), RetrieveMyRankingResDto.class);

        assertEquals(requestMember.getId(), response.memberId());
        assertEquals(1L, response.currentRank());
    }

    @Test
    @DisplayName("[FAIL] 내 랭킹 조회 - 데이터가 없으면 INVALID_REQUEST")
    void retrieveMyRanking_notFound_fail() throws Exception {
        String url = BASE_URL + "/" + emptyTopicId + "/me?year=" + YEAR + "&month=" + MONTH + "&week=" + WEEK;

        MvcResult result = this.request(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("E100"));
    }

    @Test
    @DisplayName("[FAIL] 랭킹 조회 - 필수 쿼리 파라미터 누락")
    void retrieveRankings_missingQueryParam_fail() throws Exception {
        String url = BASE_URL + "/" + mainTopicId + "?year=" + YEAR + "&month=" + MONTH;

        this.request(MockMvcRequestBuilders.get(url)).andExpect(status().isBadRequest());
    }
}
