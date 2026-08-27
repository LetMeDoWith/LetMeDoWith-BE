package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.service.TaskCategoryService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTaskCategoriesResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Task Category", description = "테스크 카테고리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/category")
public class TaskCategoryController {

    @Autowired
    private TaskCategoryService taskCategoryService;

    @Operation(summary = "태스크 카테고리 조회", description = "모든 태스크 카테고리를 조회합니다. (공통 + 유저 생성)")
    @ApiSuccessResponse(description = "카테고리 조회 성공")
    @GetMapping("")
    public ResponseEntity<ResponseDto<List<RetrieveTaskCategoriesResDto>>> getTaskCategories() {

        String memberId = AuthUtil.getMemberId();

        List<RetrieveTaskCategoriesResDto> res = taskCategoryService.retrieveTaskCategories(memberId).stream()
                .map(RetrieveTaskCategoriesResDto::from)
                .toList();

        return ResponseUtil.createSuccessResponse(res);
    }
}
