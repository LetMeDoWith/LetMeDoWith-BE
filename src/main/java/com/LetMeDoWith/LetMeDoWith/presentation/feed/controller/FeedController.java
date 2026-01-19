package com.LetMeDoWith.LetMeDoWith.presentation.feed.controller;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveDowithTaskSuccessImagesResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.service.FeedTaskService;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.feed.dto.RetrieveDowithTaskSuccessImagesRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Feeds", description = "둘러보기")
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedTaskService feedTaskService;

    @Operation(summary = "Dowith Task 인증 사진 조회")
    @GetMapping("/tasks/dowith/success-images")
    public ResponseEntity<ResponsePageDto<RetrieveDowithTaskSuccessImagesRes>> retrieveDowithTaskSuccessImages(@ParameterObject Pageable pageable) {

        RetrieveDowithTaskSuccessImagesResult result = this.feedTaskService.retrieveDowithTaskSuccessImages(pageable);

        return ResponseUtil.createSuccessResponse(
                RetrieveDowithTaskSuccessImagesRes.from(result),
                pageable,
                result.totalCount()
        );
    }

}
