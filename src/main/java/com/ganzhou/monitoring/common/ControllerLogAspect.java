package com.ganzhou.monitoring.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Description: Controller 接口日志切面。 统一记录每个接口的开始时间、结束时间和执行耗时，避免各个接口重复手写日志。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {

    /**
     * 日志时间格式。
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 拦截所有 controller 包下的公开接口方法。
     */
    @Around("execution(public * com.ganzhou.monitoring.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startMillis = System.currentTimeMillis();
        LocalDateTime startTime = LocalDateTime.now();
        String interfaceName = buildInterfaceName(joinPoint);
        String requestUri = resolveRequestUri();

        log.info("接口开始，接口名称={}, 请求路径={}, 开始时间={}, 入参={}",
                interfaceName,
                requestUri,
                DATE_TIME_FORMATTER.format(startTime),
                Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            long costMillis = System.currentTimeMillis() - startMillis;
            log.info("接口结束，接口名称={}, 请求路径={}, 结束时间={}, 耗时={}ms",
                    interfaceName,
                    requestUri,
                    DATE_TIME_FORMATTER.format(LocalDateTime.now()),
                    costMillis);
            return result;
        } catch (Throwable ex) {
            long costMillis = System.currentTimeMillis() - startMillis;
            log.error("接口异常结束，接口名称={}, 请求路径={}, 结束时间={}, 耗时={}ms, 异常信息={}",
                    interfaceName,
                    requestUri,
                    DATE_TIME_FORMATTER.format(LocalDateTime.now()),
                    costMillis,
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }

    /**
     * 组装接口名称，便于日志快速定位具体 controller 方法。
     */
    private String buildInterfaceName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }

    /**
     * 获取当前请求路径。
     * 若当前不在 Web 请求上下文，则返回 unknown。
     */
    private String resolveRequestUri() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return "unknown";
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request == null ? "unknown" : request.getRequestURI();
    }
}
