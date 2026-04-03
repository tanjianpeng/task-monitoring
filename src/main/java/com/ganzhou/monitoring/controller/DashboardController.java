package com.ganzhou.monitoring.controller;

import com.ganzhou.monitoring.common.ApiResponse;
import com.ganzhou.monitoring.dto.LinkVO;
import com.ganzhou.monitoring.dto.SummaryVO;
import com.ganzhou.monitoring.dto.TaskCardVO;
import com.ganzhou.monitoring.dto.TaskDetailVO;
import com.ganzhou.monitoring.service.DashboardService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitor/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    /**
     * 大屏查询服务。
     */
    private final DashboardService dashboardService;

    /**
     * 查询某一业务日期的大屏汇总统计。
     */
    @GetMapping("/summary")
    public ApiResponse<SummaryVO> summary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate bizDate) {
        return ApiResponse.success(dashboardService.getSummary(bizDate));
    }

    /**
     * 查询任务卡片列表。
     * pageCode 为空时，默认返回当前日期下的全部任务。
     */
    @GetMapping("/tasks")
    public ApiResponse<List<TaskCardVO>> tasks(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate bizDate,
            @RequestParam(required = false) String pageCode) {
        return ApiResponse.success(dashboardService.getTaskCards(bizDate, pageCode));
    }

    /**
     * 查询任务依赖关系，用于前端绘制连线。
     */
    @GetMapping("/links")
    public ApiResponse<List<LinkVO>> links(@RequestParam(required = false) String pageCode) {
        return ApiResponse.success(dashboardService.getLinks(pageCode));
    }

    /**
     * 查询任务详情和事件流水。
     */
    @GetMapping("/detail")
    public ApiResponse<TaskDetailVO> detail(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate bizDate,
            @RequestParam String taskCode) {
        return ApiResponse.success(dashboardService.getTaskDetail(bizDate, taskCode));
    }
}
