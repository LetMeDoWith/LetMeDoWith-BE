package com.LetMeDoWith.LetMeDoWith.presentation;

import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "헬스 체크")
@RestController
@RequestMapping("/health-check")
@RequiredArgsConstructor
public class HealthCheckController {

    @Operation(summary = "헬스 체크", description = "서비스 기동 및 기본 응답 가능 여부를 확인합니다.")
    @ApiSuccessResponse(description = "헬스 체크 성공")
    @GetMapping("")
    public ResponseEntity<ResponseDto<Void>> retrieveMemberInfo() {
        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "테스트용 예외 발생", description = "UNAUTHORIZED 예외를 발생시켜 에러 응답 포맷을 확인할 때 사용합니다.")
    @GetMapping("/test")
    public ResponseEntity<ResponseDto<Void>> testException() {

        throw new RestApiException(FailResponseStatus.UNAUTHORIZED);
    }
}
