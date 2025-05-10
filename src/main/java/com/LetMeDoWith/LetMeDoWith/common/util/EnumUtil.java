package com.LetMeDoWith.LetMeDoWith.common.util;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.CODE_ENUM_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INTERNAL_SERVER_ERROR;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EnumUtil {

    public <T extends BaseEnum> T getEnum(Class<T> enumClass, String code) {
        if (code == null) return null;

        try {
            for (BaseEnum el : enumClass.getEnumConstants()) {
                if (code.equalsIgnoreCase(el.getCode().toUpperCase())) {
                    return (T) el;
                }
            }
        } catch (Exception e) {
            throw new RestApiException(INTERNAL_SERVER_ERROR);
        }

        throw new RestApiException(CODE_ENUM_NOT_EXIST);
    }
}
