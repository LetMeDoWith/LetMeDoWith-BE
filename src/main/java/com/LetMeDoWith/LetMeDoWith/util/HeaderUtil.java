package com.LetMeDoWith.LetMeDoWith.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HeaderUtil {

	private final String USER_AGENT_KEY = "User-Agent";

	public String getUserAgent() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		return request.getHeader(USER_AGENT_KEY);
	}

}
