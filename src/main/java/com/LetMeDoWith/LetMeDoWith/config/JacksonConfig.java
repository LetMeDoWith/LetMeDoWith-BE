package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();

        // Timestamp로 출력하지 않고 ISO 8601 문자열로 출력
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // LocalTime Serializer/Deserializer 등록 (DateTimeUtil 포맷터 사용)
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeUtil.getLocalTimeFormatter()));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeUtil.getLocalTimeFormatter()));

        // Java 8 시간 모듈 등록
        mapper.registerModule(module);

        return mapper;
    }
}
