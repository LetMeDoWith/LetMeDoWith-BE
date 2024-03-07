package com.LetMeDoWith.LetMeDoWith.util;

import com.LetMeDoWith.LetMeDoWith.dto.common.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.enums.common.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.enums.common.SuccessResponseStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ResponseUtilTest {

    public SuccessResponseStatus successResponseStatus = SuccessResponseStatus.OK;
    public FailResponseStatus failResponseStatus = FailResponseStatus.BAD_REQUEST;

    @Test
    @DisplayName("성공응답 테스트 - no parameter")
    void test_success_res_no_param() {
        ResponseEntity<ResponseDto<Object>> successResponse = ResponseUtil.createSuccessResponse();

        assertEquals(successResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(successResponse.getBody().getStatusCode(), "S100");
    }

    @Test
    @DisplayName("성공응답 테스트 - with data")
    void test_success_res_with_data() {
        ResponseEntity<ResponseDto<Object>> successResponse = ResponseUtil.createSuccessResponse("data");

        assertEquals(successResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(successResponse.getBody().getStatusCode(), "S100");

        // 데이터가 올바르게 들어갔는지 확인
        assertEquals(successResponse.getBody().getData(), "data");
    }

    @Test
    @DisplayName("성공응답 테스트 - with custom header")
    void test_success_res_with_header() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("attr", "val");

        ResponseEntity<ResponseDto<Object>> successResponse = ResponseUtil.createSuccessResponse("data", httpHeaders);

        assertEquals(successResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(successResponse.getBody().getStatusCode(), "S100");

        // 데이터가 올바르게 들어갔는지 확인
        assertEquals(successResponse.getBody().getData(), "data");

        // 헤더가 올바르게 들어갔는지 확인
        assertEquals(successResponse.getHeaders().get("attr").get(0), "val");

    }

    @Test
    @DisplayName("성공응답 테스트 - with custom status")
    void test_success_res_with_status() {
        ResponseEntity<ResponseDto<Object>> successResponse = ResponseUtil.createSuccessResponse(SuccessResponseStatus.OBJECT_NOT_FOUND);

        assertEquals(successResponse.getStatusCode(), HttpStatus.OK);

        // 사용자 지정 상태와 일치하는지 확인
        assertEquals(successResponse.getBody().getStatusCode(), "S101");
    }

}