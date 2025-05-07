package com.LetMeDoWith.LetMeDoWith.integration;

import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.TaskCompleteLevel;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    AccessTokenProvider accessTokenProvider;
    private AccessToken requestMemberAccessToken;
    
    /**
     * 해당 Abstract Class 상속 받은 테스트는 모든 Test 메서드 시작전에 request Member 세팅
     */
    @BeforeEach
    void beforeEach() {
        memberJpaRepository.deleteAll();
        
        requestMember = memberJpaRepository.save(Member.builder()
                                                       .status(MemberStatus.NORMAL)
                                                       .taskCompleteLevel(TaskCompleteLevel.AVERAGE)
                                                       .nickname("test")
                                                       .selfDescription("test description")
                                                       .gender(Gender.MALE)
                                                       .dateOfBirth(LocalDate.of(1995, 11, 4))
                                                       .type(MemberType.USER)
                                                       .build());
        requestMemberAccessToken = accessTokenProvider.createAccessToken(requestMember.getId());
        
    }
    
    /**
     * MockMvc Request
     * 해당 abstract class 상속 받은 테스트에서 MockHttpServletRequestBuilder 만 넘겨서 사용
     *
     * @param requestBuilder
     * @return
     * @throws Exception
     */
    public ResultActions request(MockHttpServletRequestBuilder requestBuilder) {
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + requestMemberAccessToken.getToken());
        
        requestBuilder.headers(new HttpHeaders(headerMap))
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .characterEncoding(StandardCharsets.UTF_8);
        try {
            return mockMvc.perform(requestBuilder)
                          .andDo(System.out::println);
        } catch (Exception e) {
            log.error("request error", e);
            Assertions.fail("MockMvc 요청 중 에러 발생" + e.getMessage());
            return null;
        }
        
    }
    
}
