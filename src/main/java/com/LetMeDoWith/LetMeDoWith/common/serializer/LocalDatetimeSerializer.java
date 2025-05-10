package com.LetMeDoWith.LetMeDoWith.common.serializer;

import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class LocalDatetimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(
            LocalDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(DateTimeUtil.toFormatString(dateTime));
    }
}
