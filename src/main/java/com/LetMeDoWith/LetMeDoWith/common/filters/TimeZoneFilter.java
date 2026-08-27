package com.LetMeDoWith.LetMeDoWith.common.filters;

import com.LetMeDoWith.LetMeDoWith.common.dto.FailResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.holders.TimeZoneContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class TimeZoneFilter extends OncePerRequestFilter {

    private static final String TIME_ZONE_HEADER = "X-Time-Zone";
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String header = request.getHeader(TIME_ZONE_HEADER);

            ZoneId zone = null;
            if (header == null) {
                zone = DEFAULT_ZONE_ID;
            } else {
                try {
                    zone = ZoneId.of(header);
                } catch (Exception e) {
                    this.sendBadRequestResponse(response);
                }
            }

            TimeZoneContextHolder.setTimeZone(zone);
            filterChain.doFilter(request, response);
        } finally {
            TimeZoneContextHolder.clearTimeZoneHolder();
        }
    }

    private void sendBadRequestResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        FailResponseDto responseBody = FailResponseDto.builder()
                .statusCode(FailResponseStatus.INVALID_REQUEST.getStatusCode())
                .message("X-Time-Zone value is invalid.")
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
