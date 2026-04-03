package com.ganzhou.monitoring.service;

import com.ganzhou.monitoring.dto.TaskReportRequest;

public interface MonitoringService {

    /**
     * 处理外部系统统一任务动作上报。
     *
     * @param action 动作类型，begin/end
     * @param request 上报请求
     * @return 当前运行批次
     */
    Integer handleTaskAction(String action, TaskReportRequest request);
}
