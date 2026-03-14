package com.LetMeDoWith.LetMeDoWith.integration.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.GenerateMemberProfileImageUploadPresignedUrlReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.GenerateMemberProfileImageUploadPresignedUrlResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MemberProfileImagePresignedUrlIntegrationTest extends AbstractIntegrationTest {

    private static final String GENERATE_MEMBER_PROFILE_IMAGE_UPLOAD_PRESIGNED_URL =
            "/api/v1/members/profile-image/upload-presigned-url";

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.region}")
    private String region;

    @Override
    protected void deleteTestData() {
        // requestMember cleanup is handled by AbstractIntegrationTest
    }

    @Override
    protected void createTestData() {}

    @Test
    @DisplayName("[SUCCESS] 회원 프로필 이미지 업로드 presigned URL 발급")
    void generateMemberProfileImageUploadPresignedUrl() throws Exception {
        GenerateMemberProfileImageUploadPresignedUrlReqDto requestBody =
                new GenerateMemberProfileImageUploadPresignedUrlReqDto("profile.png");

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GENERATE_MEMBER_PROFILE_IMAGE_UPLOAD_PRESIGNED_URL)
                        .content(this.writeRequestBodyAsString(requestBody)));
        GenerateMemberProfileImageUploadPresignedUrlResDto response =
                this.readResponse(resultActions, GenerateMemberProfileImageUploadPresignedUrlResDto.class);

        String presignedUrlPrefix = String.format("https://%s.s3.%s.amazonaws.com/member_profiles", bucketName, region);

        resultActions.andExpect(status().isOk());
        assertThat(response.presignedUrl()).contains("profile.png");
        assertThat(response.presignedUrl()).contains(presignedUrlPrefix);
        assertThat(response.publicImageUrl()).contains("profile.png");
        assertThat(response.method()).isEqualTo("POST");
    }

    @Test
    @DisplayName("[FAIL] 허용되지 않은 프로필 이미지 확장자인 경우")
    void generateMemberProfileImageUploadPresignedUrlFail() throws Exception {
        GenerateMemberProfileImageUploadPresignedUrlReqDto requestBody =
                new GenerateMemberProfileImageUploadPresignedUrlReqDto("profile.pdf");

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.post(GENERATE_MEMBER_PROFILE_IMAGE_UPLOAD_PRESIGNED_URL)
                        .content(this.writeRequestBodyAsString(requestBody)));

        resultActions.andExpect(status().is4xxClientError());
    }
}
