package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class GenderConverter extends AbstractCombinedEnumConverter<Gender> {
    
    public GenderConverter() {
        super(Gender.class);
    }
    
    // TODO: @RequestBody에서는 Converter를 못쓰고, Deserializer 써야 하니 이거 구현.
    
    // Gender는 Member의 nullable 필드이기 때문에,
    // Gender 없이 Member를 save 시도하는 경우 AttributeConverter에서 NPE가 발생할 수 있어 AttributeConverter를 재정의한다.
    
    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return attribute == null ? Gender.MALE.getCode() : super.convertToDatabaseColumn(attribute);
    }
    
    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return dbData == null ? Gender.MALE : super.convertToEntityAttribute(dbData);
    }
}