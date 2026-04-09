package com.ganzhou.monitoring.controller;

import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.common.ResultDto;
import com.ganzhou.monitoring.dto.*;
import com.ganzhou.monitoring.entity.MonitorSystem;
import com.ganzhou.monitoring.entity.MonitorTaskDef;
import com.ganzhou.monitoring.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Description: 跑批监控统一控制器
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BatchMonitoringController {

    /** 跑批监控核心服务。 */
    private final MonitoringService monitoringService;

    /** 大屏查询服务。 */
    private final DashboardService dashboardService;

    /** 系统主数据服务。 */
    private final SystemManageService systemManageService;

    /** 任务主数据服务。 */
    private final TaskManageService taskManageService;

    /** 外部系统回调验签服务。 */
    private final TaskSignService taskSignService;

    /**
     * 外部系统统一上报接口。
     * 入参格式按外部接口文档兼容 systemCode、taskCode、bizDate、status、requestTime、sign。
     */
    @PostMapping("/monitor/task/report")
    public ResultDto<String> report(@RequestBody TaskReportRequest request) {
        try {
            validateExternalRequest(request.getStatus(), request);
            taskSignService.verifySign(request);
            Integer runNo = monitoringService.handleTaskAction(request.getStatus(), request);
            return ResultDto.success(String.valueOf(runNo));
        } catch (BusinessException ex) {
            return ResultDto.fail(ex.getMessage());
        } catch (Exception ex) {
            return ResultDto.fail("系统异常，请联系管理员");
        }
    }

    /**
     * 本地大屏顶部统计查询接口。
     */
    @GetMapping("/monitor/local/queryStatistics")
    public ResultDto<Map<String, String>> queryStatistics() {
        return ResultDto.success(dashboardService.getStatistics());
    }

    /**
     * 本地大屏系统列表查询接口。
     */
    @GetMapping("/monitor/local/querySystems")
    public ResultDto<List<SystemDashboardCardVO>> querySystems(@RequestParam(required = false) String systemName) {
        return ResultDto.success(dashboardService.getSystems(systemName));
    }

    /**
     * 本地大屏任务状态查询接口。
     * 返回结果已按任务依赖顺序做整理，方便前端直接按顺序渲染。
     */
    @GetMapping("/monitor/local/queryTaskStatus")
    public ResultDto<TaskStatusDataVO> queryTaskStatus() {
        return ResultDto.success(dashboardService.getTaskStatusList());
    }

    /**
     * 手工触发某日实例初始化。
     * 该接口与每天 0 点定时任务共用同一套服务逻辑，便于补数或人工重刷。
     */
    @PostMapping("/monitor/task/instances/init")
    public ResultDto<Integer> initTaskInstances(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate bizDate) {
        return ResultDto.success(monitoringService.initializeTaskInstances(bizDate));
    }

    /** 查询系统列表。 */
    @GetMapping("/manage/systems")
    public ResultDto<List<MonitorSystem>> listSystems() {
        return ResultDto.success(systemManageService.listSystems());
    }

    /** 查询系统详情。 */
    @GetMapping("/manage/systems/{systemCode}")
    public ResultDto<MonitorSystem> systemDetail(@PathVariable String systemCode) {
        return ResultDto.success(systemManageService.getSystem(systemCode));
    }

    /** 新增系统。 */
    @PostMapping("/manage/systems")
    public ResultDto<Void> createSystem(@Valid @RequestBody SystemSaveRequest request) {
        systemManageService.createSystem(request);
        return ResultDto.successMessage("系统新增成功");
    }

    /** 修改系统。 */
    @PutMapping("/manage/systems/{systemCode}")
    public ResultDto<Void> updateSystem(@PathVariable String systemCode,
                                        @Valid @RequestBody SystemSaveRequest request) {
        request.setSystemCode(systemCode);
        systemManageService.updateSystem(request);
        return ResultDto.successMessage("系统修改成功");
    }

    /** 删除系统。 */
    @DeleteMapping("/manage/systems/{systemCode}")
    public ResultDto<Void> deleteSystem(@PathVariable String systemCode) {
        systemManageService.deleteSystem(systemCode);
        return ResultDto.successMessage("系统删除成功");
    }

    /** 查询任务列表。 */
    @GetMapping("/manage/tasks")
    public ResultDto<List<MonitorTaskDef>> listTasks() {
        return ResultDto.success(taskManageService.listTasks());
    }

    /** 查询任务详情及依赖关系。 */
    @GetMapping("/manage/tasks/{taskCode}")
    public ResultDto<TaskManageDetailVO> taskManageDetail(@PathVariable String taskCode) {
        return ResultDto.success(taskManageService.getTaskDetail(taskCode));
    }

    /** 新增任务及依赖关系。 */
    @PostMapping("/manage/tasks")
    public ResultDto<Void> createTask(@Valid @RequestBody TaskSaveRequest request) {
        taskManageService.createTask(request);
        return ResultDto.successMessage("任务新增成功");
    }

    /** 修改任务及依赖关系。 */
    @PutMapping("/manage/tasks/{taskCode}")
    public ResultDto<Void> updateTask(@PathVariable String taskCode,
                                      @Valid @RequestBody TaskSaveRequest request) {
        request.setTaskCode(taskCode);
        taskManageService.updateTask(request);
        return ResultDto.successMessage("任务修改成功");
    }

    /** 删除任务及其依赖关系。 */
    @DeleteMapping("/manage/tasks/{taskCode}")
    public ResultDto<Void> deleteTask(@PathVariable String taskCode) {
        taskManageService.deleteTask(taskCode);
        return ResultDto.successMessage("任务删除成功");
    }

    /**
     * 校验外部系统上报必要字段。
     * 图片中的报文格式只要求核心字段，因此这里不走通用 @Valid 失败响应。
     */
    private void validateExternalRequest(String action, TaskReportRequest request) {
        if (request == null) {
            throw new BusinessException("请求报文不能为空");
        }
        if (request.getSystemCode() == null || request.getSystemCode().isBlank()) {
            throw new BusinessException("systemCode不能为空");
        }
        if (request.getTaskCode() == null || request.getTaskCode().isBlank()) {
            throw new BusinessException("taskCode不能为空");
        }
        if (request.getBizDate() == null) {
            throw new BusinessException("bizDate不能为空");
        }
        if ((request.getStatus() == null || request.getStatus().isBlank())
                && (action == null || action.isBlank())) {
            throw new BusinessException("status不能为空");
        }
        if (request.getRequestTime() == null) {
            throw new BusinessException("requestTime不能为空");
        }
        if (request.getSign() == null || request.getSign().isBlank()) {
            throw new BusinessException("sign不能为空");
        }
    }
}
