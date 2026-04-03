package com.LetMeDoWith.LetMeDoWith.common.util;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.FileNamespace;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 파일 namespace와 원본 파일명을 기반으로 S3 key를 생성합니다.
 */
public final class FileKeyUtil {

    private static final DateTimeFormatter FILE_KEY_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss'Z'");

    private FileKeyUtil() {}

    /**
     * 파일명 목록에 대응하는 S3 key 목록을 생성합니다.
     *
     * @param fileNamespace 업로드 파일 namespace
     * @param fileNames 원본 파일명 목록
     * @return 생성된 S3 key 목록
     */
    public static List<String> generateKeys(FileNamespace fileNamespace, List<String> fileNames) {
        return fileNames.stream()
                .map(fileName -> generateKey(fileNamespace, fileName))
                .toList();
    }

    /**
     * 단일 파일명에 대한 S3 key를 생성합니다.
     *
     * @param fileNamespace 업로드 파일 namespace
     * @param fileName 원본 파일명
     * @return 생성된 S3 key
     */
    private static String generateKey(FileNamespace fileNamespace, String fileName) {
        String timestamp = SystemTimeUtil.now().format(FILE_KEY_TIMESTAMP_FORMATTER);
        String uuid = UUID.randomUUID().toString();
        int shardIndex = Math.floorMod(uuid.hashCode(), 16);

        return String.format("%s/%02d/%s_%s_%s", fileNamespace.getPrefix(), shardIndex, timestamp, uuid, fileName);
    }
}
