package com.LetMeDoWith.LetMeDoWith.dto.valueObject;

import java.time.LocalDateTime;

public record AccessTokenVO(String token, LocalDateTime expireAt) {
}
