package com.zifang.ctc.audit.starter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private static final String JWT_SECRET = "zifang-ctc-secret-key";

    @Value("${audit.enabled:true}")
    private boolean enabled;

    private final AuditClient auditClient;

    public AuditAspect(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @Pointcut("@annotation(com.zifang.ctc.audit.starter.Audit)")
    public void auditPointcut() {}

    @Around("auditPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (!enabled) {
            return point.proceed();
        }

        long start = System.currentTimeMillis();
        HttpServletRequest request = getRequest();
        String traceId = UUID.randomUUID().toString().replace("-", "");

        String userName = null;
        Long userId = null;
        String tenantCode = null;
        if (request != null) {
            try {
                String token = extractToken(request);
                if (token != null) {
                    Claims claims = Jwts.parser()
                            .setSigningKey(JWT_SECRET.getBytes())
                            .parseClaimsJws(token)
                            .getBody();
                    userName = claims.get("userName", String.class);
                    Object userIdObj = claims.get("userId");
                    if (userIdObj instanceof Integer) userId = ((Integer) userIdObj).longValue();
                    else if (userIdObj instanceof Long) userId = (Long) userIdObj;
                    tenantCode = claims.get("tenantCode", String.class);
                }
            } catch (Exception e) {
                log.debug("JWT解析失败: {}", e.getMessage());
            }
        }

        Object result = null;
        int status = 1;
        String errorMsg = null;
        try {
            result = point.proceed();
        } catch (Throwable t) {
            status = 0;
            errorMsg = t.getMessage();
            throw t;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            try {
                AuditEvent event = new AuditEvent();
                event.setTraceId(traceId);
                event.setOperationType(determineOperationType(request));
                event.setOperationDesc(buildOperationDesc(point));
                event.setUserId(userId);
                event.setUserName(userName);
                event.setTenantCode(tenantCode);
                event.setIpAddress(request != null ? getIpAddress(request) : null);
                event.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
                event.setRequestUrl(request != null ? request.getRequestURI() : null);
                event.setRequestMethod(request != null ? request.getMethod() : null);
                event.setRequestParams(buildParams(point, request));
                event.setExecutionTime((int) executionTime);
                event.setStatus(status);
                event.setErrorMsg(errorMsg);
                event.setTimestamp(LocalDateTime.now());
                auditClient.send(event);
            } catch (Exception e) {
                log.error("审计事件构建失败: {}", e.getMessage());
            }
        }
        return result;
    }

    private String determineOperationType(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";
        switch (request.getMethod().toUpperCase()) {
            case "GET": return "QUERY";
            case "POST": return "CREATE";
            case "PUT": case "PATCH": return "UPDATE";
            case "DELETE": return "DELETE";
            default: return request.getMethod().toUpperCase();
        }
    }

    private String buildOperationDesc(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Audit annotation = method.getAnnotation(Audit.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return point.getTarget().getClass().getSimpleName() + "." + point.getSignature().getName();
    }

    private String buildParams(ProceedingJoinPoint point, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        if (request != null) {
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if (entry.getValue().length == 1) params.put(entry.getKey(), entry.getValue()[0]);
                else params.put(entry.getKey(), entry.getValue());
            }
        }
        try {
            for (Object arg : point.getArgs()) {
                if (arg == null) continue;
                if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                        || arg instanceof MultipartFile) continue;
                params.put("body", arg);
            }
        } catch (Exception ignored) {}
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(params);
        } catch (Exception ignored) {
            return "{}";
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "unknown";
    }
}
