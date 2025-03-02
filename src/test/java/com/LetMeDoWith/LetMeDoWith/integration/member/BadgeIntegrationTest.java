package com.LetMeDoWith.LetMeDoWith.integration.member;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.BADGE_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.LAZY_NOT_AVAIL_UPDATE_MAIN_BADGE;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.MEMBER_BADGE_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.TaskCompleteLevel;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Badge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberBadge;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.BadgeJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberBadgeJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.UpdateMainBadgeReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class BadgeIntegrationTest {
    
    static final String BASE_URL = "/api/v1/member/badge";
    static final String RETRIEVE_BADGES_INFO_URL = "";
    static final String UPDATE_MAIN_BADGE = "/main";
    
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccessTokenProvider accessTokenProvider;
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    BadgeJpaRepository badgeJpaRepository;
    @Autowired
    MemberBadgeJpaRepository memberBadgeJpaRepository;
    
    Member member;
    Member lazyMember;
    AccessToken memberAccessToken;
    AccessToken lazyMemberAccessToken;
    
    Badge badge1;
    Badge badge2;
    Badge badge3;
    Badge badge4;
    MemberBadge memberBadge1;
    MemberBadge memberBadge2;
    MemberBadge memberBadge3;
    
    @BeforeEach
    void beforeEach() {
        
        member = memberJpaRepository.save(Member.builder()
                                                .status(MemberStatus.NORMAL)
                                                .taskCompleteLevel(TaskCompleteLevel.AVERAGE)
                                                .nickname("test")
                                                .selfDescription("test description")
                                                .gender(Gender.MALE)
                                                .dateOfBirth(LocalDate.of(1995, 11, 4))
                                                .type(MemberType.USER)
                                                .build());
        
        lazyMember = memberJpaRepository.save(Member.builder()
                                                    .status(MemberStatus.NORMAL)
                                                    .taskCompleteLevel(TaskCompleteLevel.BAD)
                                                    .nickname("test2")
                                                    .selfDescription("test description2")
                                                    .gender(Gender.FEMALE)
                                                    .dateOfBirth(LocalDate.of(1995, 11, 4))
                                                    .type(MemberType.USER)
                                                    .build());
        
        memberAccessToken = accessTokenProvider.createAccessToken(member.getId());
        lazyMemberAccessToken = accessTokenProvider.createAccessToken(lazyMember.getId());
        
    }
    
    @AfterEach
    void afterEach() {
        
        memberBadgeJpaRepository.deleteAll();
        badgeJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
        
    }
    
    private ResultActions requestRetrieveBadgesInfo(AccessToken accessToken) throws Exception {
        
        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + accessToken.getToken());
        
        return mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + RETRIEVE_BADGES_INFO_URL)
                                                     .headers(new HttpHeaders(headerMap))
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .accept(MediaType.APPLICATION_JSON)
                                                     .characterEncoding(StandardCharsets.UTF_8))
                      .andDo(System.out::println);
        
    }
    
    private ResultActions requestUpdateMainBadge(AccessToken accessToken,
                                                 UpdateMainBadgeReqDto requestBody)
        throws Exception {
        
        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + accessToken.getToken());
        
        return mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + UPDATE_MAIN_BADGE)
                                                     .headers(new HttpHeaders(headerMap))
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .accept(MediaType.APPLICATION_JSON)
                                                     .characterEncoding(StandardCharsets.UTF_8)
                                                     .content(objectMapper.writeValueAsString(
                                                         requestBody)))
                      .andDo(System.out::println);
        
    }
    
    private void insertTestData() {
        
        BadgeStatus badge1Status = BadgeStatus.ACTIVE;
        String badge1Name = "뱃지1";
        String badge1Description = "뱃지1 description";
        String badge1ActiveHint = "뱃지1 힌트";
        String badge1ImageUrl = "뱃지1 이미지 url";
        int badge1SortOrder = 2;
        
        BadgeStatus badge2Status = BadgeStatus.ACTIVE;
        String badge2Name = "뱃지2";
        String badge2Description = "뱃지2 description";
        String badge2ActiveHint = "뱃지2 힌트";
        String badge2ImageUrl = "뱃지2 이미지 url";
        int badge2SortOrder = 1;
        
        BadgeStatus badge3Status = BadgeStatus.ACTIVE;
        String badge3Name = "뱃지3";
        String badge3Description = "뱃지3 description";
        String badge3ActiveHint = "뱃지3 힌트";
        String badge3ImageUrl = "뱃지3 이미지 url";
        int badge3SortOrder = 3;
        
        BadgeStatus badge4Status = BadgeStatus.INACTIVE;
        String badge4Name = "뱃지4";
        String badge4Description = "뱃지4 description";
        String badge4ActiveHint = "뱃지4 힌트";
        String badge4ImageUrl = "뱃지4 이미지 url";
        int badge4SortOrder = 4;
        
        badge1 = badgeJpaRepository.save(Badge.builder()
                                              .badgeStatus(badge1Status)
                                              .name(badge1Name)
                                              .description(badge1Description)
                                              .acquireHint(badge1ActiveHint)
                                              .imageUrl(badge1ImageUrl)
                                              .sortOrder(badge1SortOrder)
                                              .build());
        
        badge2 = badgeJpaRepository.save(Badge.builder()
                                              .badgeStatus(badge2Status)
                                              .name(badge2Name)
                                              .description(badge2Description)
                                              .acquireHint(badge2ActiveHint)
                                              .imageUrl(badge2ImageUrl)
                                              .sortOrder(badge2SortOrder)
                                              .build());
        
        badge3 = badgeJpaRepository.save(Badge.builder()
                                              .badgeStatus(badge3Status)
                                              .name(badge3Name)
                                              .description(badge3Description)
                                              .acquireHint(badge3ActiveHint)
                                              .imageUrl(badge3ImageUrl)
                                              .sortOrder(badge3SortOrder)
                                              .build());
        
        badge4 = badgeJpaRepository.save(Badge.builder()
                                              .badgeStatus(badge4Status)
                                              .name(badge4Name)
                                              .description(badge4Description)
                                              .acquireHint(badge4ActiveHint)
                                              .imageUrl(badge4ImageUrl)
                                              .sortOrder(badge4SortOrder)
                                              .build());
        
        // badge1이 대표 뱃지
        memberBadge1 = memberBadgeJpaRepository.save(MemberBadge.builder()
                                                                .memberId(member.getId())
                                                                .badge(badge1)
                                                                .isMain(Yn.TRUE)
                                                                .build());
        
        memberBadge2 = memberBadgeJpaRepository.save(MemberBadge.builder()
                                                                .memberId(member.getId())
                                                                .badge(badge2)
                                                                .isMain(Yn.FALSE)
                                                                .build());
        
        memberBadge3 = memberBadgeJpaRepository.save(MemberBadge.builder()
                                                                .memberId(lazyMember.getId())
                                                                .badge(badge3)
                                                                .isMain(Yn.FALSE)
                                                                .build());
        
        badgeJpaRepository.flush();
        memberJpaRepository.flush();
    }
    
    @Test
    @DisplayName("[SUCCESS] 뱃지 목록 조회 - member")
    void retrieveBadgesInfoTest() throws Exception {
        
        // given
        insertTestData();
        
        // when
        ResultActions resultActions = requestRetrieveBadgesInfo(memberAccessToken);
        
        // then
        resultActions.andExpect(status().is2xxSuccessful())
                     .andExpect(jsonPath("$.data.isLazyMember").value(false))
                     .andExpect(jsonPath("$.data.mainBadge").exists())
                     .andExpect(jsonPath("$.data.mainBadge.id").value(badge1.getId()))
                     .andExpect(jsonPath("$.data.mainBadge.name").value(badge1.getName()))
                     .andExpect(jsonPath("$.data.mainBadge.imageUrl").value(badge1.getImageUrl()))
                     .andExpect(jsonPath("$.data.mainBadge.description").value(badge1.getDescription()))
                     .andExpect(jsonPath("$.data.badges").isArray())
                     .andExpect(jsonPath("$.data.badges", hasSize(3)))
                     .andExpect(jsonPath("$.data.badges[0].id").value(badge2.getId()))
                     .andExpect(jsonPath("$.data.badges[0].isAcquired").value(true))
                     .andExpect(jsonPath("$.data.badges[0].name").value(badge2.getName()))
                     .andExpect(jsonPath("$.data.badges[0].imageUrl").value(badge2.getImageUrl()))
                     .andExpect(jsonPath("$.data.badges[0].description").value(badge2.getDescription()))
                     .andExpect(jsonPath("$.data.badges[0].acquireHint").value(badge2.getAcquireHint()))
                     .andExpect(jsonPath("$.data.badges[1].id").value(badge1.getId()))
                     .andExpect(jsonPath("$.data.badges[1].isAcquired").value(true))
                     .andExpect(jsonPath("$.data.badges[1].name").value(badge1.getName()))
                     .andExpect(jsonPath("$.data.badges[1].imageUrl").value(badge1.getImageUrl()))
                     .andExpect(jsonPath("$.data.badges[1].description").value(badge1.getDescription()))
                     .andExpect(jsonPath("$.data.badges[1].acquireHint").value(badge1.getAcquireHint()))
                     .andExpect(jsonPath("$.data.badges[2].id").value(badge3.getId()))
                     .andExpect(jsonPath("$.data.badges[2].isAcquired").value(false))
                     .andExpect(jsonPath("$.data.badges[2].name").value(badge3.getName()))
                     .andExpect(jsonPath("$.data.badges[2].imageUrl").value(badge3.getImageUrl()))
                     .andExpect(jsonPath("$.data.badges[2].description").value(badge3.getDescription()))
                     .andExpect(jsonPath("$.data.badges[2].acquireHint").value(badge3.getAcquireHint()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[SUCCESS] 뱃지 목록 조회 - lazyMember")
    void retrieveBadgesInfoTest2() throws Exception {
        
        // given
        insertTestData();
        
        // when
        ResultActions resultActions = requestRetrieveBadgesInfo(lazyMemberAccessToken);
        
        // then
        resultActions.andExpect(status().is2xxSuccessful())
                     .andExpect(jsonPath("$.data.isLazyMember").value(true))
                     .andExpect(jsonPath("$.data.mainBadge").doesNotExist())
//        .andExpect(jsonPath("$.data.mainBadge.dowithTaskId").value(badge1.getId()))
//        .andExpect(jsonPath("$.data.mainBadge.name").value(badge1.getName()))
//        .andExpect(jsonPath("$.data.mainBadge.imageUrl").value(badge1.getImageUrl()))
//        .andExpect(jsonPath("$.data.mainBadge.description").value(badge1.getDescription()))
                     .andExpect(jsonPath("$.data.badges").isArray())
                     .andExpect(jsonPath("$.data.badges", hasSize(3)))
                     .andExpect(jsonPath("$.data.badges[0].id").value(badge2.getId()))
                     .andExpect(jsonPath("$.data.badges[0].isAcquired").value(false))
                     .andExpect(jsonPath("$.data.badges[0].name").value(badge2.getName()))
                     .andExpect(jsonPath("$.data.badges[0].imageUrl").value(badge2.getImageUrl()))
                     .andExpect(jsonPath("$.data.badges[0].description").value(badge2.getDescription()))
                     .andExpect(jsonPath("$.data.badges[0].acquireHint").value(badge2.getAcquireHint()))
                     .andExpect(jsonPath("$.data.badges[1].id").value(badge1.getId()))
                     .andExpect(jsonPath("$.data.badges[1].isAcquired").value(false))
                     .andExpect(jsonPath("$.data.badges[1].name").value(badge1.getName()))
                     .andExpect(jsonPath("$.data.badges[1].imageUrl").value(badge1.getImageUrl()))
                     .andExpect(jsonPath("$.data.badges[1].description").value(badge1.getDescription()))
                     .andExpect(jsonPath("$.data.badges[1].acquireHint").value(badge1.getAcquireHint()))
                     .andExpect(jsonPath("$.data.badges[2].id").value(badge3.getId()))
                     .andExpect(jsonPath("$.data.badges[2].isAcquired").value(true))
                     .andExpect(jsonPath("$.data.badges[2].name").value(badge3.getName()))
                     .andExpect(jsonPath("$.data.badges[2].imageUrl").value(badge3.getImageUrl()))
                     .andExpect(jsonPath("$.data.badges[2].description").value(badge3.getDescription()))
                     .andExpect(jsonPath("$.data.badges[2].acquireHint").value(badge3.getAcquireHint()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[SUCCESS] 메인 뱃지 업데이트")
    void updateMainBadgeSuccessTest1() throws Exception {
        
        // given
        insertTestData();
        UpdateMainBadgeReqDto requestBody = new UpdateMainBadgeReqDto(badge2.getId());
        
        // when
        ResultActions resultActions = requestUpdateMainBadge(memberAccessToken, requestBody);
        MemberBadge oldMemberBadge = memberBadgeJpaRepository.findByMemberIdAndBadge(
            member.getId(), badge1).orElseThrow(() -> new IllegalArgumentException("not found"));
        MemberBadge newMemberBadge = memberBadgeJpaRepository.findByMemberIdAndBadge(
            member.getId(), badge2).orElseThrow(() -> new IllegalArgumentException("not found"));
        
        // then
        resultActions.andExpect(status().is2xxSuccessful());
        assertThat(oldMemberBadge.getIsMain()).isEqualTo(Yn.FALSE);
        assertThat(newMemberBadge.getIsMain()).isEqualTo(Yn.TRUE);
        
    }
    
    @Test
    @DisplayName("[FAIL] 메인 뱃지 업데이트 - LAZY 뱃지 보유자로 업데이트 불가")
    void updateMainBadgeFailTest1() throws Exception {
        
        // given
        insertTestData();
        UpdateMainBadgeReqDto requestBody = new UpdateMainBadgeReqDto(badge2.getId());
        
        // when
        ResultActions resultActions = requestUpdateMainBadge(lazyMemberAccessToken, requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(LAZY_NOT_AVAIL_UPDATE_MAIN_BADGE.getStatusCode()))
                     .andExpect(jsonPath("$.message").value(LAZY_NOT_AVAIL_UPDATE_MAIN_BADGE.getMessage()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] 메인 뱃지 업데이트 - 뱃지가 존재하지 않는 경우")
    void updateMainBadgeFailTest2() throws Exception {
        
        // given
        insertTestData();
        UpdateMainBadgeReqDto requestBody = new UpdateMainBadgeReqDto(1000L);
        
        // when
        ResultActions resultActions = requestUpdateMainBadge(memberAccessToken, requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(BADGE_NOT_EXIST.getStatusCode()))
                     .andExpect(jsonPath("$.message").value(BADGE_NOT_EXIST.getMessage()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] 메인 뱃지 업데이트 - 획득한 뱃지가 아닌 경우")
    void updateMainBadgeFailTest3() throws Exception {
        
        // given
        insertTestData();
        UpdateMainBadgeReqDto requestBody = new UpdateMainBadgeReqDto(badge3.getId());
        
        // when
        ResultActions resultActions = requestUpdateMainBadge(memberAccessToken, requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(MEMBER_BADGE_NOT_EXIST.getStatusCode()))
                     .andExpect(jsonPath("$.message").value(MEMBER_BADGE_NOT_EXIST.getMessage()))
                     .andDo(System.out::println);
        
    }
    
    
}