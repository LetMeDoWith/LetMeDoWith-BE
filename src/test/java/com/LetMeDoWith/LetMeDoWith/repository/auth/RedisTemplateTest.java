package com.LetMeDoWith.LetMeDoWith.repository.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.LetMeDoWith.LetMeDoWith.config.RedisConfig;

@SpringBootTest()
public class RedisTemplateTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	@DisplayName("RedisTemplate 정상 작동 테스트")
	void redisTemplateSuccessTest() {
		redisTemplate.opsForValue()
			.set("hello", new HelloDto("name", "desc"));


	}


	public class HelloDto {
		private String name;
		private String desc;

		public HelloDto(String name, String desc) {
			this.name = name;
			this.desc = desc;
		}
	}
}
