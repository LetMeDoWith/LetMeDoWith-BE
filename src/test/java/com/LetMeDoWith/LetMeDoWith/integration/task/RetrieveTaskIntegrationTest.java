package com.LetMeDoWith.LetMeDoWith.integration.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class RetrieveTaskIntegrationTest {
    
    static final String BASE_URL = "/api/v1/task/dowith";
    
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    
    
}
