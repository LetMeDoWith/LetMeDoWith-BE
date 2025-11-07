package com.LetMeDoWith.LetMeDoWith.presentation.notice.controller;

import com.LetMeDoWith.LetMeDoWith.application.notice.dto.CreateNoticeCommand;
import com.LetMeDoWith.LetMeDoWith.application.notice.service.CreateNoticeService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.notice.dto.CreateNoticeReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "공지사항/이벤트")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final CreateNoticeService createNoticeService;

    @Operation(summary = "공지사항/이벤트 생성", description = "새로운 공지사항/이벤트 게시글을 추가합니다")
    @ApiSuccessResponse(description = "새로운 공지 추가 성공")
    @PostMapping("")
    public ResponseEntity<ResponseDto<Null>> createNotice(@RequestBody CreateNoticeReqDto req) {

        createNoticeService.createNotice(CreateNoticeCommand.of(
                req.type(),
                req.title(),
                req.content(),
                req.startDateTime(),
                req.endDateTime(),
                req.thumbnailImageUrl()));

        return ResponseUtil.createSuccessResponse();
    }
}
