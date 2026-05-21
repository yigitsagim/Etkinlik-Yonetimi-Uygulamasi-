package com.works.etkinlikyonetimiuygulamasi.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SessionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // CORS headers ekle
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // OPTIONS preflight isteğini geç
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String urlPath = request.getRequestURI();

        String[] freeUrls = {
                "/users/register",
                "/users/login",
                "/users/logout",
                "/swagger-ui",
                "/v3/api-docs",
                "/actuator"
        };

        boolean isAuth = true;
        for (String freeUrl : freeUrls) {
            if (urlPath.startsWith(freeUrl)) {
                isAuth = false;
                break;
            }
        }

        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String method = request.getMethod();
        String query = request.getQueryString();
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        HttpSession session = request.getSession(false);
        Object user = (session != null) ? session.getAttribute("user") : null;

        logger.info("""
                ====== REQUEST LOG ======
                Time      : {}
                IP        : {}
                Method    : {}
                URL       : {}
                Query     : {}
                UserAgent : {}
                Session   : {}
                User      : {}
                ==========================
                """,
                time, ipAddress, method, urlPath, query, userAgent,
                (session != null ? session.getId() : "No Session"),
                (user != null ? user : "Anonymous")
        );

        if (isAuth && user == null) {
            logger.warn("Unauthorized access -> IP: {}, URL: {}", ipAddress, urlPath);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write("""
                    {
                      "success": false,
                      "message": "Unauthorized access. Please log in."
                    }
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}