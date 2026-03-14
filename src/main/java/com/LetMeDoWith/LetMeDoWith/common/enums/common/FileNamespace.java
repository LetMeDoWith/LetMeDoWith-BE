package com.LetMeDoWith.LetMeDoWith.common.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 업로드 파일이 저장될 S3 prefix namespace를 정의합니다.
 */
@AllArgsConstructor
@Getter
public enum FileNamespace {
    DOWITH_TASK_CONFIRM("dowith_task_confirms");

    private final String prefix;
}
