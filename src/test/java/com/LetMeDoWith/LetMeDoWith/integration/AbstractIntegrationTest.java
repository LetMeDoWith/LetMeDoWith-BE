package com.LetMeDoWith.LetMeDoWith.integration;

import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.RefreshTokenProvider;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskSummaryJpaRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected Member requestMember;
    protected TaskSummary taskSummary;

    @Autowired
    protected TaskSummaryJpaRepository taskSummaryJpaRepository;

    protected RefreshToken requestMemberRefreshToken;

    @Autowired
    protected MemberJpaRepository memberJpaRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccessTokenProvider accessTokenProvider;

    @Autowired
    RefreshTokenProvider refreshTokenProvider;

    private AccessToken requestMemberAccessToken;

    @BeforeEach
    void beforeEach() {
        createMemberTestData();
        createTestData();
    }

    @AfterEach
    void afterEach() {
        deleteTestData();
        memberJpaRepository.deleteAll();
    }

    /**
     * Test 환경 시간대 설정
     *
     * @param dateTime
     */
    protected void setFixedClock(LocalDateTime dateTime) {
        SystemTimeUtil.setClock(Clock.fixed(dateTime.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
    }

    protected String writeRequestBodyAsString(Object requestBody) {
        try {
            return this.objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            log.error("writeRequestBodyAsString error", e);
            Assertions.fail("Request Body String 변환 중 에러 발생" + e.getMessage());
            return null;
        }
    }

    /**
     * 해당 Abstract Class 상속 받은 테스트는 모든 Test 메서드 시작전에 request Member 세팅
     */
    private void createMemberTestData() {
        requestMember = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("test")
                .selfDescription("test description")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1995, 11, 4))
                .type(MemberType.USER)
                .build());
        taskSummary = taskSummaryJpaRepository.save(TaskSummary.of(requestMember.getId()));
        requestMemberAccessToken = accessTokenProvider.generateToken(requestMember.getId());
        requestMemberRefreshToken = refreshTokenProvider.generateToken(
                requestMember.getId(), requestMemberAccessToken.getToken(), "iphone"); // TODO - 추후 안드로이드 유져 하나 추가
    }

    /**
     * 이전 Test의 test data 삭제 - abstract method
     */
    protected abstract void deleteTestData();

    /**
     * Test Data 생성 - abstract method
     */
    protected abstract void createTestData();

    /**
     * MockMvc Request 해당 abstract class 상속 받은 테스트에서 MockHttpServletRequestBuilder 만 넘겨서 사용
     *
     * @param requestBuilder
     * @return
     * @throws Exception
     */
    public ResultActions request(MockHttpServletRequestBuilder requestBuilder) {
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + requestMemberAccessToken.getToken());
        headerMap.add("User-Agent", requestMemberRefreshToken.getUserAgent());

        requestBuilder
                .headers(new HttpHeaders(headerMap))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);
        try {
            return mockMvc.perform(requestBuilder).andDo(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("MockMvc 요청 중 에러 발생" + e.getMessage());
            return null;
        }
    }

    /**
     * MockMvc ResultActions를 통해 받은 응답을 지정된 타입으로 변환합니다.
     *
     * @param resultActions
     * @param responseBodyType
     * @param <T>
     * @return
     */
    public <T> T readResponse(ResultActions resultActions, Class<T> responseBodyType) {
        try {
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            return readResponse(responseBody, responseBodyType);
        } catch (Exception e) {
            log.error("readResponse error", e);
            Assertions.fail("MockMvc 응답 변환 중 에러 발생" + e.getMessage());
            return null;
        }
    }

    /**
     * MockMvc ResultActions를 통해 받은 응답을 지정된 타입으로 변환합니다.
     *
     * @param resultActions
     * @param responseBodyType
     * @param <T>
     * @return
     */
    public <T> T readPagingResponse(ResultActions resultActions, Class<T> responseBodyType) {
        try {
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            return readPagingResponse(responseBody, responseBodyType);
        } catch (Exception e) {
            log.error("readResponse error", e);
            Assertions.fail("MockMvc 응답 변환 중 에러 발생" + e.getMessage());
            return null;
        }
    }

    /**
     * MockMvc ResultActions를 통해 받은 응답을 지정된 타입으로 변환합니다.
     *
     * @param responseBody     API Response 원문
     * @param responseBodyType 변환하려는 응답 타입
     * @param <T>              변환하려는 응답 타입의 제네릭
     * @return 변환된 응답 객체
     */
    public <T> T readResponse(String responseBody, Class<T> responseBodyType) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(ResponseDto.class, responseBodyType);

            ResponseDto<T> responseDto = objectMapper.readValue(responseBody, type);

            return responseDto.data();
        } catch (Exception e) {
            log.error("readResponse error", e);
            Assertions.fail("Response 변환 중 에러 발생" + e.getMessage());
            return null;
        }
    }

    public <T> T readPagingResponse(String responseBody, Class<T> responseBodyType) {
        try {
            JavaType type =
                    objectMapper.getTypeFactory().constructParametricType(ResponsePageDto.class, responseBodyType);

            ResponsePageDto<T> responsePageDto = objectMapper.readValue(responseBody, type);

            return responsePageDto.data();
        } catch (Exception e) {
            log.error("readResponse error", e);
            Assertions.fail("Response 변환 중 에러 발생" + e.getMessage());
            return null;
        }
    }
}
