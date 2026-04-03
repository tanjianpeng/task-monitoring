package com.ganzhou.monitoring.controller;

import com.ganzhou.monitoring.common.ApiResponse;
import com.ganzhou.monitoring.dto.TaskManageDetailVO;
import com.ganzhou.monitoring.dto.TaskSaveRequest;
import com.ganzhou.monitoring.entity.MonitorTaskDef;
import com.ganzhou.monitoring.service.TaskManageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务主数据管理接口。
 * 支持任务表增删改查，并在保存任务时同时维护依赖关系。
 */
@RestController
@RequestMapping("/api/manage/tasks")
@RequiredArgsConstructor
public class TaskManageController {

    private final TaskManageService taskManageService;

    /** 查询任务列表。 */
    @GetMapping
    public ApiResponse<List<MonitorTaskDef>> list() {
        return ApiResponse.success(taskManageService.listTasks());
    }

    /** 查询任务详情及依赖关系。 */
    @GetMapping("/{taskCode}")
    public ApiResponse<TaskManageDetailVO> detail(@PathVariable String taskCode) {
        return ApiResponse.success(taskManageService.getTaskDetail(taskCode));
    }

    /** 新增任务及依赖关系。 */
    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody TaskSaveRequest request) {
        taskManageService.createTask(request);
        return ApiResponse.successMessage("任务新增成功");
    }

    /** 修改任务及依赖关系。 */
    @PutMapping("/{taskCode}")
    public ApiResponse<Void> update(@PathVariable String taskCode,
                                    @Valid @RequestBody TaskSaveRequest request) {
        request.setTaskCode(taskCode);
        taskManageService.updateTask(request);
        return ApiResponse.successMessage("任务修改成功");
    }

    /** 删除任务及其依赖关系。 */
    @DeleteMapping("/{taskCode}")
    public ApiResponse<Void> delete(@PathVariable String taskCode) {
        taskManageService.deleteTask(taskCode);
        return ApiResponse.successMessage("任务删除成功");
    }
}
