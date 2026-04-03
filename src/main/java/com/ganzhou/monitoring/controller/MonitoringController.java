package com.ganzhou.monitoring.controller;

import com.ganzhou.monitoring.common.ApiResponse;
import com.ganzhou.monitoring.dto.TaskReportRequest;
import com.ganzhou.monitoring.service.MonitoringService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitor/task")
@RequiredArgsConstructor
public class MonitoringController {

    /**
     * 跑批监控核心服务。
     */
    private final MonitoringService monitoringService;

    /**
     * 统一任务动作上报接口。
     * action 取值：
     * 1. begin：任务开始
     * 2. end：任务结束
     *
     * @param params 动作类型
     * @param request 请求体
     * @return 标准响应
     */
    @PostMapping("/{params}")
    public ApiResponse<Map<String, Object>> report(@PathVariable String params,
                                                   @Valid @RequestBody TaskReportRequest request) {
        Integer runNo = monitoringService.handleTaskAction(params, request);
        Map<String, Object> data = new HashMap<>();
        data.put("accepted", true);
        data.put("runNo", runNo);
        return ApiResponse.success(data);
    }
}
