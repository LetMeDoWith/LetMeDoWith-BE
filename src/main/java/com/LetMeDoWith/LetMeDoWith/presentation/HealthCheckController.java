package com.LetMeDoWith.LetMeDoWith.presentation;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("")
    public ResponseEntity retrieveMemberInfo() {
        return ResponseUtil.createSuccessResponse();
    }

    @GetMapping("/test")
    public ResponseEntity testException() {

        throw new RestApiException(FailResponseStatus.UNAUTHORIZED);
    }
}
