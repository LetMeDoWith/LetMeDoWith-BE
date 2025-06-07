package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record GenerateDowithTaskConfirmImageUploadPresignedUrlsReqDto(
        @Schema(description = "업로드할 파일 이름(확장자 포함)", example = "[\"photo1.jpg\", \"photo2.jpg\"]")
                List<String> imageFileNames) {}
