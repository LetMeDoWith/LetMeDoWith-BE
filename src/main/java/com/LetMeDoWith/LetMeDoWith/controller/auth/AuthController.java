package com.LetMeDoWith.LetMeDoWith.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LetMeDoWith.LetMeDoWith.dto.requestDto.CreateTokenRefreshReqDto;
import com.LetMeDoWith.LetMeDoWith.dto.responseDto.CreateTokenRefreshResDto;
import com.LetMeDoWith.LetMeDoWith.service.AuthService;
import com.LetMeDoWith.LetMeDoWith.util.HeaderUtil;
import com.LetMeDoWith.LetMeDoWith.util.ResponseUtil;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/token/refresh")
	public ResponseEntity createTokenRefresh(@RequestBody CreateTokenRefreshReqDto requestBody) {

		String userAgent = HeaderUtil.getUserAgent();

		CreateTokenRefreshResDto result = authService.createTokenRefresh(requestBody.accessToken(),
			requestBody.refreshToken(), userAgent);

		return ResponseUtil.createSuccessResponse(result);
	}
}
