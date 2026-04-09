package com.ganzhou.monitoring.service;

import java.util.List;

import com.ganzhou.monitoring.dto.TaskManageDetailVO;
import com.ganzhou.monitoring.dto.TaskSaveRequest;
import com.ganzhou.monitoring.entity.MonitorTaskDef;

/**
 * Description: 任务主数据管理服务。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public interface TaskManageService {

    List<MonitorTaskDef> listTasks();

    TaskManageDetailVO getTaskDetail(String taskCode);

    void createTask(TaskSaveRequest request);

    void updateTask(TaskSaveRequest request);

    void deleteTask(String taskCode);
}
