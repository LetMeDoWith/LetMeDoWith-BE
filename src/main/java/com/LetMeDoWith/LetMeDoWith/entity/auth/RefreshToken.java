package com.LetMeDoWith.LetMeDoWith.entity.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@RedisHash("refreshToken")
@AllArgsConstructor
@RequiredArgsConstructor
public class RefreshToken {

	@Id
	private String token;

	private String accessToken;

	private Long memberId;
	private String userAgent;

	@TimeToLive
	private Long expireSec;

}
