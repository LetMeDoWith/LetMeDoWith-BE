package com.LetMeDoWith.LetMeDoWith.presentation.notice.controller;

import com.LetMeDoWith.LetMeDoWith.application.notice.dto.RetrieveNoticeDetailResult;
import com.LetMeDoWith.LetMeDoWith.application.notice.dto.RetrieveNoticesResult;
import com.LetMeDoWith.LetMeDoWith.application.notice.service.RetrieveNoticeService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.notice.dto.RetrieveNoticeDetailResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.notice.dto.RetrieveNoticesResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "공지사항/이벤트")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final RetrieveNoticeService retrieveNoticeService;

    @Operation(summary = "공지사항/이벤트 목록 조회", description = "공지사항/이벤트 목록을 조회합니다. 페이징 가능")
    @ApiSuccessResponse(description = "공지 조회")
    @GetMapping("")
    public ResponseEntity<ResponsePageDto<RetrieveNoticesResDto>> retrieveNotices(
            @RequestParam(required = false) NoticeType type, @ParameterObject Pageable pageable) {

        RetrieveNoticesResult result = retrieveNoticeService.retrieveNotices(type, pageable);
        RetrieveNoticesResDto res = RetrieveNoticesResDto.from(result);

        return ResponseUtil.createSuccessResponse(res, pageable, result.totalCount());
    }

    @Operation(summary = "공지사항/이벤트 상세 조회", description = "공지사항/이벤트의 상세 내용을 조회합니다.")
    @ApiSuccessResponse(description = "공지 상세 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<ResponseDto<RetrieveNoticeDetailResDto>> retrieveNoticeDetail(@PathVariable Long noticeId) {
        RetrieveNoticeDetailResult result = retrieveNoticeService.retrieveNoticeDetail(noticeId);

        return ResponseUtil.createSuccessResponse(RetrieveNoticeDetailResDto.of(
                result.id(),
                result.title(),
                result.content(),
                result.type(),
                result.createdAt(),
                result.thumbnailImageUrl()));
    }
}
