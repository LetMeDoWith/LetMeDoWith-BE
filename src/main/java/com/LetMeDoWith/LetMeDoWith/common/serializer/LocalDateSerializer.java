package com.LetMeDoWith.LetMeDoWith.common.serializer;

import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDate;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(
            LocalDate date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(DateTimeUtil.toFormatString(date));
    }
}
