package com.LetMeDoWith.LetMeDoWith.presentation.ranking.controller;

import com.LetMeDoWith.LetMeDoWith.application.ranking.service.RetrieveRankingService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveMyRankingResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveRankingTopicsResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto.RetrieveRankingsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseUtil.createSuccessResponse(
                RetrieveRankingTopicsResDto.from(retrieveRankingService.retrieveRankingTopics()));
    }

    @Operation(summary = "랭킹 상세 조회", description = "랭킹 상세 내역을 조회합니다.")
    @ApiSuccessResponse(description = "랭킹 상세 조회 성공")
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ResponseDto<RetrieveRankingsResDto>> retrieveRankings(
            @Parameter(description = "랭킹 주제 ID", example = "1") @PathVariable Long topicId,
            @Parameter(description = "조회할 랭킹 라운드", example = "3") @RequestParam Long round,
            @Parameter(description = "상위 N명까지 조회 (기본 5)", example = "5") @RequestParam(defaultValue = "5")
                    Integer size) {
        if (size < 1) {
            throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
        }
        return ResponseUtil.createSuccessResponse(
                RetrieveRankingsResDto.from(retrieveRankingService.retrieveRankings(topicId, round, size)));
    }

    @Operation(summary = "내 랭킹 조회", description = "특정 토픽에 대한 내 랭킹을 조회합니다.")
    @ApiSuccessResponse(description = "내 랭킹 조회 성공")
    @GetMapping("/topic/{topicId}/me")
    public ResponseEntity<ResponseDto<RetrieveMyRankingResDto>> retrieveMyRanking(
            @Parameter(description = "랭킹 주제 ID", example = "1") @PathVariable Long topicId,
            @Parameter(description = "조회할 랭킹 라운드", example = "3") @RequestParam Long round) {
        return ResponseUtil.createSuccessResponse(
                RetrieveMyRankingResDto.from(retrieveRankingService.retrieveMyRanking(topicId, round)));
    }
}
