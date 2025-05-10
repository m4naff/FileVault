package com.devlab.filevault.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitingFilter implements Filter {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket getBucket(String clientId) {
        return buckets.computeIfAbsent(clientId, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.classic(10, Refill.intervally(1, Duration.ofSeconds(1))))
                        .build());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String clientIp = httpRequest.getRemoteAddr();
        Bucket bucket = getBucket(clientIp);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            servletResponse.getWriter().write("Too many requests.");
            servletResponse.getWriter().flush();
        }
    }
}
