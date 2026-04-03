package com.ganzhou.monitoring.common;

/**
 * 业务异常。
 * 用于承载可预期的业务校验错误，例如主数据不存在、编码重复、状态不合法等。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
