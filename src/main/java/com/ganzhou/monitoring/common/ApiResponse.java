package com.ganzhou.monitoring.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 返回码。
     * 约定：0000 表示成功，其余编码表示失败。
     */
    private String code;

    /**
     * 返回信息。
     */
    private String message;

    /**
     * 业务数据。
     */
    private T data;

    /**
     * 构造成功响应，并携带业务数据。
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("0000", "success", data);
    }

    /**
     * 构造成功响应，只返回提示信息，不返回业务数据。
     */
    public static <T> ApiResponse<T> successMessage(String message) {
        return new ApiResponse<>("0000", message, null);
    }

    /**
     * 构造失败响应。
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>("9999", message, null);
    }
}
