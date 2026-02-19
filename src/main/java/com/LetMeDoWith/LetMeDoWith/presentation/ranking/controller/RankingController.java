package com.LetMeDoWith.LetMeDoWith.presentation.ranking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.LetMeDoWith.LetMeDoWith.application.ranking.service.RetrieveRankingService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveMyRankingResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveRankingsResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveRankingTopicsResDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
@Tag(name = "Ranking", description = "랭킹")
public class RankingController {
    private final RetrieveRankingService retrieveRankingService;

    @Operation(summary = "랭킹 목록 조회", description = "랭킹 목록을 조회합니다.")
    @ApiSuccessResponse(description = "랭킹 목록 조회 성공")
    @GetMapping("")
    public ResponseEntity<ResponseDto<RetrieveRankingTopicsResDto>> retrieveRankingTopics() {
        return ResponseUtil.createSuccessResponse(RetrieveRankingTopicsResDto.from(retrieveRankingService.retrieveRankingTopics()));
    }

    @Operation(summary = "랭킹 조회", description = "랭킹을 조회합니다.")
    @ApiSuccessResponse(description = "랭킹 조회 성공")
    @GetMapping("/topicId}")
    public ResponseEntity<ResponseDto<RetrieveRankingsResDto>> retrieveRankings(@PathVariable Long topicId, @RequestParam Integer year, @RequestParam Integer month, @RequestParam Integer week, @RequestParam(defaultValue = "5") Integer limit) {
        return ResponseUtil.createSuccessResponse(RetrieveRankingsResDto.from(retrieveRankingService.retrieveRankingsByTopicId(topicId, year, month, week, limit)));
    }

    @Operation(summary = "내 랭킹 조회", description = "내 랭킹을 조회합니다.")
    @ApiSuccessResponse(description = "내 랭킹 조회 성공")
    @GetMapping("/{topicId}/me")
    public ResponseEntity<ResponseDto<RetrieveMyRankingResDto>> retrieveMyRanking(@PathVariable Long topicId, @RequestParam Integer year, @RequestParam Integer month, @RequestParam Integer week) {
        String memberId = AuthUtil.getMemberId();
        return ResponseUtil.createSuccessResponse(RetrieveMyRankingResDto.from(retrieveRankingService.retrieveMyRanking(memberId, topicId, year, month, week)));
    }
}
