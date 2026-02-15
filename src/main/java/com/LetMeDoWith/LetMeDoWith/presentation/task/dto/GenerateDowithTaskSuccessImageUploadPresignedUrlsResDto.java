package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import java.util.List;

public record GenerateDowithTaskSuccessImageUploadPresignedUrlsResDto(
        List<String> publicImageUrls, List<String> presignedUrls, String method) {}
