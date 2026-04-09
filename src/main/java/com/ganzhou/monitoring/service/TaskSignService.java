package com.ganzhou.monitoring.service;

import com.ganzhou.monitoring.dto.TaskReportRequest;

/**
 * Description: 外部系统回调验签服务。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public interface TaskSignService {

    /**
     * 校验外部回调签名。
     *
     * @param request 外部回调请求
     */
    void verifySign(TaskReportRequest request);
}
