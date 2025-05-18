package com.LetMeDoWith.LetMeDoWith.presentation.member.controller;

import com.LetMeDoWith.LetMeDoWith.application.member.service.FollowService;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.FollowType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.CreateFollowReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.RetrieveFollowsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member Follow", description = "회원 팔로우")
@RestController
@RequestMapping("/api/v1/member/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우 목록 조회", description = "유져의 팔로우 목록을 조회합니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity retrieveFollows(
            @PathVariable(name = "memberId") String memberId,
            @RequestParam(name = "followType") FollowType type,
            Pageable pageable) {

        String tokenMemberId = AuthUtil.getMemberId();
        if (!tokenMemberId.equals(memberId)) {
            throw new RestApiException(FailResponseStatus.UNAUTHORIZED);
        }

        RetrieveFollowsResDto result = followService.retrieveFollows(memberId, type, pageable);

        return ResponseUtil.createSuccessResponse(result, pageable);
    }

    @Operation(summary = "팔로우 등록", description = "유져의 팔로우 대상을 등록합니다.")
    @PostMapping()
    public ResponseEntity createFollow(@RequestBody CreateFollowReqDto requestBody) {

        followService.createFollow(AuthUtil.getMemberId(), requestBody.followMemberId());

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "팔로우 취소", description = "팔로우를 취소합니다.")
    @DeleteMapping("/{followingId}")
    public ResponseEntity deleteFollow(@PathVariable String followingId) {

        followService.deleteFollow(AuthUtil.getMemberId(), followingId);

        return ResponseUtil.createSuccessResponse();
    }
}
