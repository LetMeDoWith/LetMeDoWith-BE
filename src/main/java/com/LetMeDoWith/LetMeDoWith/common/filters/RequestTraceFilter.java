package com.LetMeDoWith.LetMeDoWith.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTraceFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    } // async 재진입 방지

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // UUID(32hex)에서 앞 8자리만 사용
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        MDC.put("traceId", traceId);

        try {
            String url = req.getRequestURI();
            String qs = req.getQueryString();
            log.info("REQUEST: {} {}{}", req.getMethod(), url, (qs != null ? "?" + qs : ""));
            chain.doFilter(req, res);
        } finally {
            MDC.remove("traceId");
        }
    }
}
