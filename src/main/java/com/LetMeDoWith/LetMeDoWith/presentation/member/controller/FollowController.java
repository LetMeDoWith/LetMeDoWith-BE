package com.LetMeDoWith.LetMeDoWith.presentation.member.controller;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.RetrieveFollowsResult;
import com.LetMeDoWith.LetMeDoWith.application.member.service.FollowService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.FollowType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.CreateFollowReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.RetrieveFollowsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member Follow", description = "회원 팔로우")
@RestController
@RequestMapping("/api/v1/members/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우 목록 조회", description = "유져의 팔로우 목록을 조회합니다.")
    @ApiSuccessResponse(description = "팔로우 목록 조회 성공")
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<ResponsePageDto<RetrieveFollowsResDto>> retrieveFollows(
            @Parameter(description = "조회 대상 회원 ID (본인만 가능)", example = "01234567890123456789012345")
                    @PathVariable(name = "memberId")
                    String memberId,
            @Parameter(description = "FOLLOWER: 나를 팔로우한 목록 / FOLLOWING: 내가 팔로우한 목록", example = "FOLLOWING")
                    @RequestParam(name = "followType")
                    FollowType type,
            @ParameterObject Pageable pageable) {

        String tokenMemberId = AuthUtil.getMemberId();
        if (!tokenMemberId.equals(memberId)) {
            throw new RestApiException(FailResponseStatus.UNAUTHORIZED);
        }

        RetrieveFollowsResult result = followService.retrieveFollows(memberId, type, pageable);

        return ResponseUtil.createSuccessResponse(RetrieveFollowsResDto.of(result), pageable, result.totalCount());
    }

    @Operation(summary = "팔로우 등록", description = "유져의 팔로우 대상을 등록합니다.")
    @ApiSuccessResponse(description = "팔로우 등록 성공")
    @PostMapping()
    public ResponseEntity<ResponseDto<Void>> createFollow(@RequestBody CreateFollowReqDto requestBody) {

        followService.createFollow(AuthUtil.getMemberId(), requestBody.followMemberId());

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "팔로우 취소", description = "팔로우를 취소합니다.")
    @ApiSuccessResponse(description = "팔로우 취소 성공")
    @DeleteMapping("/{followingId}")
    public ResponseEntity<ResponseDto<Void>> deleteFollow(
            @Parameter(description = "언팔로우할 상대 회원 ID (TSID)", example = "01234567890123456789012345") @PathVariable
                    String followingId) {

        followService.deleteFollow(AuthUtil.getMemberId(), followingId);

        return ResponseUtil.createSuccessResponse();
    }
}
