package com.ganzhou.monitoring.service.impl;

import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.dto.TaskDependencyItem;
import com.ganzhou.monitoring.dto.TaskManageDetailVO;
import com.ganzhou.monitoring.dto.TaskSaveRequest;
import com.ganzhou.monitoring.entity.MonitorTaskDef;
import com.ganzhou.monitoring.entity.MonitorTaskDependency;
import com.ganzhou.monitoring.mapper.MonitorSystemManageMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskDependencyManageMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskManageMapper;
import com.ganzhou.monitoring.service.TaskManageService;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务主数据管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class TaskManageServiceImpl implements TaskManageService {

    private final MonitorTaskManageMapper taskManageMapper;
    private final MonitorTaskDependencyManageMapper dependencyManageMapper;
    private final MonitorSystemManageMapper systemManageMapper;

    @Override
    public List<MonitorTaskDef> listTasks() {
        return taskManageMapper.selectAll();
    }

    @Override
    public TaskManageDetailVO getTaskDetail(String taskCode) {
        TaskManageDetailVO detail = new TaskManageDetailVO();
        detail.setTask(taskManageMapper.selectByCode(taskCode));
        detail.setDependencies(toDependencyItems(dependencyManageMapper.selectByTaskCode(taskCode)));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTask(TaskSaveRequest request) {
        validateTaskRequest(request);
        if (taskManageMapper.selectByCode(request.getTaskCode()) != null) {
            throw new BusinessException("任务编码已存在: " + request.getTaskCode());
        }
        taskManageMapper.insert(toTaskEntity(request));
        replaceDependencies(request.getTaskCode(), request.getDependencies());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(TaskSaveRequest request) {
        validateTaskRequest(request);
        if (taskManageMapper.selectByCode(request.getTaskCode()) == null) {
            throw new BusinessException("任务不存在: " + request.getTaskCode());
        }
        taskManageMapper.updateByCode(toTaskEntity(request));
        replaceDependencies(request.getTaskCode(), request.getDependencies());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(String taskCode) {
        dependencyManageMapper.deleteByTaskCode(taskCode);
        taskManageMapper.deleteByCode(taskCode);
    }

    private void validateTaskRequest(TaskSaveRequest request) {
        if (systemManageMapper.selectByCode(request.getSystemCode()) == null) {
            throw new BusinessException("所属系统不存在: " + request.getSystemCode());
        }
    }

    private MonitorTaskDef toTaskEntity(TaskSaveRequest request) {
        MonitorTaskDef entity = new MonitorTaskDef();
        entity.setTaskCode(request.getTaskCode());
        entity.setTaskName(request.getTaskName());
        entity.setSystemCode(request.getSystemCode());
        entity.setOwnerName(request.getOwnerName());
        entity.setSupervisorName(request.getSupervisorName());
        entity.setPreRequisiteProd(request.getPreRequisiteProd());
        entity.setTheBatchProd(request.getTheBatchProd());
        entity.setPlanStartTime(request.getPlanStartTime());
        entity.setPlanEndTime(request.getPlanEndTime());
        entity.setDefaultCostMinutes(defaultInt(request.getDefaultCostMinutes(), 0));
        entity.setAvgCostMinutes(defaultInt(request.getAvgCostMinutes(), 0));
        entity.setDelayMinutes(defaultInt(request.getDelayMinutes(), 10));
        entity.setFrequency(request.getFrequency());
        entity.setDisplayFlag(defaultStr(request.getDisplayFlag(), "1"));
        entity.setPosX(defaultInt(request.getPosX(), 0));
        entity.setPosY(defaultInt(request.getPosY(), 0));
        entity.setStatus(defaultStr(request.getStatus(), "1"));
        entity.setRemark(request.getRemark());
        return entity;
    }

    private void replaceDependencies(String taskCode, List<TaskDependencyItem> dependencies) {
        dependencyManageMapper.deleteByTaskCode(taskCode);
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }
        List<MonitorTaskDependency> entityList = new ArrayList<>();
        for (TaskDependencyItem item : dependencies) {
            MonitorTaskDependency entity = new MonitorTaskDependency();
            entity.setId(UUID.randomUUID().toString().replace("-", ""));
            entity.setTaskCode(taskCode);
            entity.setPreTaskCode(item.getPreTaskCode());
            entity.setDirection(item.getDirection());
            entityList.add(entity);
        }
        dependencyManageMapper.batchInsert(entityList);
    }

    private List<TaskDependencyItem> toDependencyItems(List<MonitorTaskDependency> entities) {
        List<TaskDependencyItem> list = new ArrayList<>();
        for (MonitorTaskDependency entity : entities) {
            TaskDependencyItem item = new TaskDependencyItem();
            item.setPreTaskCode(entity.getPreTaskCode());
            item.setDirection(entity.getDirection());
            list.add(item);
        }
        return list;
    }

    private String defaultStr(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private Integer defaultInt(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }
}
