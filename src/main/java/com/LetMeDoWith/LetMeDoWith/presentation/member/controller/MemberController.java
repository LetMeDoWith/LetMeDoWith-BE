package com.LetMeDoWith.LetMeDoWith.presentation.member.controller;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateTokenResult;
import com.LetMeDoWith.LetMeDoWith.application.auth.service.CreateTokenService;
import com.LetMeDoWith.LetMeDoWith.application.member.dto.CreateSignupCompletedMemberCommand;
import com.LetMeDoWith.LetMeDoWith.application.member.service.MemberService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.GenerateUploadPresignedUrlsResult;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.SuccessResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.CheckNicknameReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.GenerateMemberProfileImageUploadPresignedUrlReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.GenerateMemberProfileImageUploadPresignedUrlResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.RetrieveMyDowithResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.SignupCompleteReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.UpdateMemberInfoReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.UpdateMemberTermAgreeReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final CreateTokenService createTokenService;

    /**
     * 회원가입을 완료한다. 회원가입이 완료되면 로그인한다.
     *
     * @param signupCompleteReqDto 회원가입을 완료하려는 멤버의 추가 정보 및 약관 동의 정보
     * @return 로그인 결과. 액세스 트큰과, refresh 토큰
     */
    @Operation(summary = "회원가입", description = "회원가입을 완료하고 로그인합니다.")
    @ApiSuccessResponse(description = "회원가입 완료, 회원 정보를 업데이트하고 로그인을 완료함 (토큰 발급).")
    @ApiErrorResponses({
        @ApiErrorResponse(
                status = FailResponseStatus.MEMBER_NOT_EXIST,
                description = "SIGNUP TOKEN 을 통해 얻은 memberId가 존재하지 않을 때 발생"),
        @ApiErrorResponse(status = FailResponseStatus.DUPLICATE_NICKNAME),
        @ApiErrorResponse(
                status = FailResponseStatus.TOKEN_EXPIRED_BY_ADMIN,
                description = "ATK가 운영자에 의해 강제로 만료됨. 재시도 필요")
    })
    @PutMapping("")
    public ResponseEntity<ResponseDto<CreateTokenResDto>> completeSignup(
            @RequestBody SignupCompleteReqDto signupCompleteReqDto) {
        CreateSignupCompletedMemberCommand command = CreateSignupCompletedMemberCommand.builder()
                .nickname(signupCompleteReqDto.nickname())
                .dateOfBirth(signupCompleteReqDto.dateOfBirth())
                .gender(signupCompleteReqDto.gender())
                .isTerms(signupCompleteReqDto.agreements().termsOfAgree())
                .isPrivacy(signupCompleteReqDto.agreements().privacy())
                .isAdvertisement(signupCompleteReqDto.agreements().advertisement())
                .build();

        Member signupCompletedMember = memberService.createSignupCompletedMember(command);
        CreateTokenResult createTokenResult = createTokenService.createToken(signupCompletedMember);

        return ResponseUtil.createSuccessResponse(
                SuccessResponseStatus.OK, CreateTokenResDto.fromCreateTokenResult(createTokenResult));
    }

    /**
     * 닉네임의 중복 여부를 검증한다. 닉네임은 공백일 수 없다.
     *
     * @param checkNicknameReqDto 중복 여부를 검증하려는 닉네임
     * @return 닉네임의 검증 결과
     */
    @Operation(summary = "닉네임 중복 여부 검증", description = "닉네임 중복 여부를 검증합니다.")
    @ApiSuccessResponse(description = "사용 가능한 닉네임")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.DUPLICATE_NICKNAME)})
    @PostMapping("/nickname")
    public ResponseEntity<ResponseDto<String>> checkNickname(@RequestBody CheckNicknameReqDto checkNicknameReqDto) {
        if (memberService.isExistingNickname(checkNicknameReqDto.nickname())) {
            throw new RestApiException(FailResponseStatus.DUPLICATE_NICKNAME);
        } else {
            return ResponseUtil.createSuccessResponse("사용 가능한 닉네임입니다.");
        }
    }

    /**
     * 멤버를 탈퇴처리한다.
     *
     * @return 탈퇴 성공 여부
     */
    @Operation(summary = "탈퇴", description = "해당 회원을 탈퇴 처리 합니다.")
    @ApiSuccessResponse(description = "회원 탈퇴 완료")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.MEMBER_NOT_EXIST)})
    @DeleteMapping("")
    public <T> ResponseEntity<ResponseDto<T>> withdrawMember() {
        String memberId = AuthUtil.getMemberId();
        memberService.withdrawMember(memberId);

        return ResponseUtil.createSuccessResponse(SuccessResponseStatus.OK);
    }

    /**
     * 회원의 약관 동의 정보를 업데이트한다.
     *
     * @param updateMemberTermAgreeReqDto 약관 동의 정보
     * @return 약관 동의 정보 업데이트 완료
     */
    @Operation(summary = "약관 동의 정보 업데이트", description = "회원의 약관 동의 정보를 업데이트합니다.")
    @ApiSuccessResponse(description = "약관 동의 정보 업데이트 완료")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.MEMBER_NOT_EXIST)})
    @PatchMapping("/agreements")
    public <T> ResponseEntity<ResponseDto<T>> updateMemberTermAgree(
            @RequestBody UpdateMemberTermAgreeReqDto updateMemberTermAgreeReqDto) {
        String memberId = AuthUtil.getMemberId();

        memberService.updateMemberTermAgree(
                memberId,
                updateMemberTermAgreeReqDto.isTermsAgree(),
                updateMemberTermAgreeReqDto.isPrivacyAgree(),
                updateMemberTermAgreeReqDto.isAdvertisementAgree());
        return ResponseUtil.createSuccessResponse(SuccessResponseStatus.OK);
    }

    /**
     * 회원의 정보를 업데이트한다.
     *
     * @param updateMemberInfoReqDto 정보 업데이트 요청
     * @return 정보 업데이트 완료
     */
    @Operation(summary = "회원 정보 업데이트", description = "회원의 정보를 업데이트합니다.")
    @ApiSuccessResponse(description = "회원 정보 업데이트 완료")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.MEMBER_NOT_EXIST)})
    @PatchMapping("")
    public <T> ResponseEntity<ResponseDto<T>> updateMemberInfo(
            @RequestBody UpdateMemberInfoReqDto updateMemberInfoReqDto) {
        String memberId = AuthUtil.getMemberId();

        memberService.updateMemberInfo(
                memberId,
                updateMemberInfoReqDto.nickname(),
                updateMemberInfoReqDto.selfDescription(),
                updateMemberInfoReqDto.profileImageUrl());
        return ResponseUtil.createSuccessResponse(SuccessResponseStatus.OK);
    }

    @Operation(summary = "프로필 이미지 upload presigned url 발급")
    @ApiSuccessResponse(description = "프로필 이미지 업로드용 presigned url 발급 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @PostMapping("/profile-image/upload-presigned-url")
    public ResponseEntity<ResponseDto<GenerateMemberProfileImageUploadPresignedUrlResDto>>
            generateMemberProfileImageUploadPresignedUrl(
                    @RequestBody GenerateMemberProfileImageUploadPresignedUrlReqDto requestBody) {
        String memberId = AuthUtil.getMemberId();

        GenerateUploadPresignedUrlsResult result =
                memberService.generateMemberProfileImageUploadPresignedUrl(memberId, requestBody.imageFileName());

        return ResponseUtil.createSuccessResponse(new GenerateMemberProfileImageUploadPresignedUrlResDto(
                result.publicImageUrls().get(0), result.presignedUrls().get(0), result.method()));
    }

    @Operation(summary = "내 마이두윗 정보 조회", description = "내 기본 정보와 전체 기간 성공한 두윗 갯수를 조회합니다.")
    @ApiSuccessResponse(description = "내 마이두윗 정보 조회 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.MEMBER_NOT_EXIST)})
    @GetMapping("/me/my-dowith")
    public ResponseEntity<ResponseDto<RetrieveMyDowithResDto>> retrieveMyDowithInfo() {
        String memberId = AuthUtil.getMemberId();
        return ResponseUtil.createSuccessResponse(
                RetrieveMyDowithResDto.from(memberService.retrieveMyDowithInfo(memberId)));
    }

    @Operation(summary = "특정 멤버의 마이두윗 정보 조회", description = "특정 멤버의 기본 정보와 전체 기간 성공한 두윗 갯수를 조회합니다.")
    @ApiSuccessResponse(description = "마이두윗 정보 조회 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.MEMBER_NOT_EXIST)})
    @GetMapping("/{memberId}/my-dowith")
    public ResponseEntity<ResponseDto<RetrieveMyDowithResDto>> retrieveMemberDowithInfo(@PathVariable String memberId) {
        return ResponseUtil.createSuccessResponse(
                RetrieveMyDowithResDto.from(memberService.retrieveMyDowithInfo(memberId)));
    }
}
