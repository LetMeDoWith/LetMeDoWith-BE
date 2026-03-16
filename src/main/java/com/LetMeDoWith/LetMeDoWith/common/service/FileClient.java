package com.LetMeDoWith.LetMeDoWith.common.service;

import java.time.Duration;

/**
 * 파일 업로드용 presigned URL 발급 구현을 추상화한 포트입니다.
 */
public interface FileClient {

    /**
     * 지정한 S3 key에 대해 업로드용 presigned URL을 발급합니다.
     *
     * @param key 업로드 대상 key
     * @param expires presigned URL 만료 시간
     * @return 업로드에 사용할 presigned URL
     */
    String getUploadPresignedUrl(String key, Duration expires);
}
