package com.LetMeDoWith.LetMeDoWith.presentation.feed.controller;

import com.LetMeDoWith.LetMeDoWith.application.feed.service.FeedDowithTaskService;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.feed.dto.RetrieveSuccessDowithTasksRes;
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

    private final FeedDowithTaskService feedDowithTaskService;

    @Operation(summary = "성공 Dowith Task 조회 (인증 사진 조회)")
    @GetMapping("/tasks/dowith/success")
    public ResponseEntity<ResponsePageDto<RetrieveSuccessDowithTasksRes>> retrieveSuccessDowithTasks(
            @ParameterObject Pageable pageable) {

        String requestMemberId = AuthUtil.getMemberId();
        var result = this.feedDowithTaskService.retrieveSuccessDowithTasks(requestMemberId, pageable);
        return ResponseUtil.createSuccessResponse(
                RetrieveSuccessDowithTasksRes.from(result), pageable, result.totalCount());
    }
}
