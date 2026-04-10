package com.ganzhou.monitoring.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 统一返回对象。 所有接口统一使用该结构返回，便于前后端约定一致。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto<T> {

    /** 返回码。 */
    private String code;

    /** 返回信息。 */
    private String message;

    /** 业务数据。 */
    private T data;

    /**
     * 成功响应。
     *
     * @param data 业务数据
     */
    public static <T> ResultDto<T> success(T data) {
        return new ResultDto<>("0000", "success", data);
    }

    /**
     * 成功响应，仅返回提示信息。
     *
     * @param message 成功提示信息
     */
    public static <T> ResultDto<T> successMessage(String message) {
        return new ResultDto<>("0000", message, null);
    }

    /**
     * 失败响应。
     *
     * @param message 失败提示信息
     */
    public static <T> ResultDto<T> fail(String message) {
        return new ResultDto<>("9999", message, null);
    }
}
