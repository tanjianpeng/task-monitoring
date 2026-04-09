package com.ganzhou.monitoring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 任务主数据管理服务实现。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class TaskManageServiceImpl implements TaskManageService {

    /**
     * 大屏箭头方向允许值。
     */
    private static final Set<String> ALLOWED_DIRECTIONS = Set.of("TOP", "BOTTOM", "LEFT", "RIGHT");

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
        if (request.getPlanEndTime().isBefore(request.getPlanStartTime())) {
            throw new BusinessException("预计结束时间不能早于预计开始时间");
        }
        validateDependencies(request.getTaskCode(), request.getDependencies());
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
        entity.setPosX(defaultInt(request.getPosX(), 0));
        entity.setPosY(defaultInt(request.getPosY(), 0));
        entity.setIsFlag(defaultStr(request.getIsFlag(), "0"));
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
            entity.setDirection(defaultDirection(item.getDirection()));
            entityList.add(entity);
        }
        dependencyManageMapper.batchInsert(entityList);
    }

    private List<TaskDependencyItem> toDependencyItems(List<MonitorTaskDependency> entities) {
        List<TaskDependencyItem> list = new ArrayList<>();
        for (MonitorTaskDependency entity : entities) {
            TaskDependencyItem item = new TaskDependencyItem();
            item.setPreTaskCode(entity.getPreTaskCode());
            item.setDirection(defaultDirection(entity.getDirection()));
            list.add(item);
        }
        return list;
    }

    /**
     * 校验依赖任务及箭头方向。
     * 页面大屏只关心箭头朝向，因此这里统一限制为四个方向并给默认值。
     */
    private void validateDependencies(String taskCode, List<TaskDependencyItem> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }
        for (TaskDependencyItem item : dependencies) {
            if (item.getPreTaskCode() == null || item.getPreTaskCode().isBlank()) {
                throw new BusinessException("依赖任务编码不能为空");
            }
            if (taskCode.equals(item.getPreTaskCode())) {
                throw new BusinessException("任务不能依赖自身");
            }
            if (taskManageMapper.selectByCode(item.getPreTaskCode()) == null) {
                throw new BusinessException("依赖任务不存在: " + item.getPreTaskCode());
            }
            String direction = defaultDirection(item.getDirection());
            if (!ALLOWED_DIRECTIONS.contains(direction)) {
                throw new BusinessException("箭头方向仅支持 TOP、BOTTOM、LEFT、RIGHT");
            }
        }
    }

    /**
     * 依赖箭头默认向右展示，便于大屏初始布局渲染。
     */
    private String defaultDirection(String direction) {
        return direction == null || direction.isBlank() ? "RIGHT" : direction.trim().toUpperCase();
    }

    private String defaultStr(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private Integer defaultInt(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }
}
