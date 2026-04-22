package com.ganzhou.monitoring.common;

/**
 * Description: 业务异常。 用于承载可预期的业务校验错误，例如主数据不存在、编码重复、状态不合法等。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public class BusinessException extends RuntimeException {

    /**
     * 构造业务异常。
     *
     * @param message 异常提示信息
     */
    public BusinessException(String message) {
        super(message);
    }
}
