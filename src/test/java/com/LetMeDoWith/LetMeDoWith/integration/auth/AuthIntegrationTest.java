// package com.LetMeDoWith.LetMeDoWith.integration.auth;
//
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
// import com.LetMeDoWith.LetMeDoWith.application.auth.provider.RefreshTokenProvider;
// import com.LetMeDoWith.LetMeDoWith.application.auth.service.CreateTokenService;
// import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
// import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
// import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
// import com.LetMeDoWith.LetMeDoWith.common.enums.task.TaskCompleteLevel;
// import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
// import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
// import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
// import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberJpaRepository;
// import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenRefreshReqDto;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.nio.charset.StandardCharsets;
// import java.time.LocalDate;
// import lombok.extern.slf4j.Slf4j;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
//
// @Slf4j
// @SpringBootTest
//// @ActiveProfiles("local")
// @AutoConfigureMockMvc
// public class AuthIntegrationTest {
//
//    static final String BASE_URL = "/api/v1/auth";
//    static final String TOKEN_REFRESH_URL = "/token/refresh";
//    static String userAgent = "IPHONE";
//    @Autowired
//    ObjectMapper objectMapper;
//    @Autowired
//    MockMvc mockMvc;
//    @Autowired
//    AccessTokenProvider accessTokenProvider;
//    @Autowired
//    RefreshTokenProvider refreshTokenProvider;
//    @Autowired
//    CreateTokenService createTokenService;
//    @Autowired
//    MemberJpaRepository memberJpaRepository;
//    Member member;
//
//    @BeforeEach
//    void beforeEach() {
//
//        member = Member.builder()
//                       .status(MemberStatus.NORMAL)
//                       .nickname("test")
//                       .selfDescription("test description")
//                       .gender(Gender.MALE)
//                       .dateOfBirth(LocalDate.of(1995, 11, 4))
//                       .type(MemberType.USER)
//                       .taskCompleteLevel(TaskCompleteLevel.AVERAGE)
//                       .build();
//
//        memberJpaRepository.save(member);
//
//    }
//
//    @AfterEach
//    void afterEach() {
//
//        // memberJpaRepository.delete(member);
//
//    }
//
//    private ResultActions requestTokenRefresh(String accessToken,
//                                              CreateTokenRefreshReqDto requestBody)
//        throws Exception {
//
//        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
//        headerMap.add("AUTHORIZATION", "Bearer" + accessToken);
//        headerMap.add("User-Agent", userAgent);
//
//        return mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + TOKEN_REFRESH_URL)
//                                                     .headers(new HttpHeaders(headerMap))
//                                                     .contentType(MediaType.APPLICATION_JSON)
//                                                     .accept(MediaType.APPLICATION_JSON)
//                                                     .characterEncoding(StandardCharsets.UTF_8)
//                                                     .content(objectMapper.writeValueAsString(
//                                                         requestBody)))
//                      .andDo(System.out::println);
//
//    }
//
//    @Test
//    @DisplayName("[SUCCESS] 토큰 재발급")
//    void refreshTokenSuccessTest() throws Exception {
//
//        // given
//        AccessToken accessToken = accessTokenProvider.createAccessToken(member.getId());
//        RefreshToken refreshToken = refreshTokenProvider.createRefreshToken(member.getId(),
//                                                                         accessToken.getToken(),
//                                                                         userAgent);
//        CreateTokenRefreshReqDto requestBody = CreateTokenRefreshReqDto.builder()
//
// .refreshToken(refreshToken.getToken())
//                                                                       .build();
//        log.info("init accessToken = {}", accessToken.getToken());
//        log.info("init refreshToken = {}", refreshToken.getToken());
//
//        // when
//        ResultActions resultActions = requestTokenRefresh(accessToken.getToken(), requestBody);
//
//        // then
//        resultActions.andExpect(status().is2xxSuccessful())
//                     .andExpect(MockMvcResultMatchers.jsonPath("$.data.atk.token").exists())
//                     .andExpect(MockMvcResultMatchers.jsonPath("$.data.rtk.token").exists())
//                     .andExpect(MockMvcResultMatchers.jsonPath("$.data.atk.expireAt").exists())
//                     .andExpect(MockMvcResultMatchers.jsonPath("$.data.rtk.expireAt").exists())
//                     .andDo(System.out::println);
//
//    }
//
// }
