package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ConfirmDowithTaskReqDto(
        @Schema(description = "업로드된 파일의 public url", example = "[\"https://bucket/image.jpg\", \"https://bucket/image2.jpg\"]") List<String> publicImageUrls) {
}
