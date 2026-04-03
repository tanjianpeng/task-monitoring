package com.ganzhou.monitoring.service;

import com.ganzhou.monitoring.dto.TaskManageDetailVO;
import com.ganzhou.monitoring.dto.TaskSaveRequest;
import com.ganzhou.monitoring.entity.MonitorTaskDef;
import java.util.List;

/**
 * 任务主数据管理服务。
 */
public interface TaskManageService {

    List<MonitorTaskDef> listTasks();

    TaskManageDetailVO getTaskDetail(String taskCode);

    void createTask(TaskSaveRequest request);

    void updateTask(TaskSaveRequest request);

    void deleteTask(String taskCode);
}
