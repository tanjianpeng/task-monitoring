package com.ganzhou.monitoring.service;

import java.time.LocalDate;

import com.ganzhou.monitoring.dto.TaskReportRequest;

/**
 * Description: 监控服务接口。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public interface MonitoringService {

    /**
     * 处理外部系统统一任务动作上报。
     *
     * @param action 动作类型，支持 start/end/restart/fail
     * @param request 上报请求
     * @return 当前运行批次
     */
    Integer handleTaskAction(String action, TaskReportRequest request);

    /**
     * 按业务日期初始化当天任务实例。
     * 该方法会根据任务定义预生成实例，供大屏提前展示，并支持定时跑批调用。
     *
     * @param bizDate 业务日期
     * @return 本次新增或刷新的实例数量
     */
    int initializeTaskInstances(LocalDate bizDate);
}
