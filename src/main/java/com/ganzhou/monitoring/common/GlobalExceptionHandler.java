package com.ganzhou.monitoring.common;

import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Description: 全局异常处理器。 统一拦截控制层抛出的异常，保证所有接口都返回一致的 JSON 结构。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     * 这类异常通常是服务层主动抛出的可预期错误。
     */
    @ExceptionHandler(BusinessException.class)
    public ResultDto<Void> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return ResultDto.fail(ex.getMessage());
    }

    /**
     * 处理参数校验异常。
     * 适用于 @Valid + RequestBody 方式的对象校验。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDto<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("；"));
        return ResultDto.fail(message);
    }

    /**
     * 处理表单绑定异常。
     * 适用于 query 参数、path 参数等绑定失败的场景。
     */
    @ExceptionHandler(BindException.class)
    public ResultDto<Void> handleBindException(BindException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("；"));
        return ResultDto.fail(message);
    }

    /**
     * 处理约束校验异常。
     * 适用于单个参数上的约束校验错误。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultDto<Void> handleConstraintViolation(ConstraintViolationException ex,
                                                       HttpServletRequest request) {
        return ResultDto.fail(ex.getMessage());
    }

    /**
     * 处理请求体格式错误。
     * 例如 JSON 格式错误、日期格式转换失败等。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultDto<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        return ResultDto.fail("请求报文格式错误，请检查JSON结构或字段类型");
    }

    /**
     * 处理非法参数异常。
     * 一般由代码中的主动校验抛出。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultDto<Void> handleIllegalArgumentException(IllegalArgumentException ex,
                                                            HttpServletRequest request) {
        return ResultDto.fail(ex.getMessage());
    }

    /**
     * 兜底处理未知异常。
     * 防止异常直接抛到前端，影响页面体验。
     */
    @ExceptionHandler(Exception.class)
    public ResultDto<Void> handleException(Exception ex, HttpServletRequest request) {
        return ResultDto.fail("系统异常，请联系管理员");
    }

    /**
     * 拼接字段错误信息。
     */
    private String formatFieldError(FieldError error) {
        return error.getField() + ":" + error.getDefaultMessage();
    }
}
