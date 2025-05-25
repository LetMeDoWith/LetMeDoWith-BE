package com.LetMeDoWith.LetMeDoWith.common.converter;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.EnumUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import org.springframework.core.convert.converter.Converter;

/**
 * Enum에 대해 HTTP 요청, DB의 입출력 요청을 변환하는 클래스 이 클래스를 상속하는 것 만으로 아래의 3개를 모두 구현한다
 *
 * <p>1. HTTP RequestParam을 변환하는 Converter 2. HTTP Request 내 Json을 변환하는 Deserializer 3. DB 입출력을 변환하는
 * JPA AttributeConverter
 *
 * @param <T> Converter가 필요한 Enum
 */
public abstract class AbstractCombinedEnumConverter<T extends BaseEnum> extends JsonDeserializer<T>
        implements Converter<String, T>, AttributeConverter<T, String> {

    private final Class<T> targetClass;

    public AbstractCombinedEnumConverter(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    // Spring Converter method
    @Override
    public T convert(String source) {
        try {
            return EnumUtil.getEnum(targetClass, source);
        } catch (Exception e) {
            throw new RestApiException(FailResponseStatus.BAD_REQUEST);
        }
    }

    // JPA AttributeConverter methods
    @Override
    public String convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            return EnumUtil.getEnum(targetClass, dbData);
        } catch (Exception e) {
            // 적절한 Exception 구현 후 대치
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            String code = p.getText();

            return EnumUtil.getEnum(targetClass, code);
        } catch (Exception e) {
            throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
        }
    }
}
