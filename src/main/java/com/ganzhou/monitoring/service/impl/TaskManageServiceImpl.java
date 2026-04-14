package com.ganzhou.monitoring.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class TaskManageServiceImpl implements TaskManageService {

    /**
     * 大屏箭头方向允许值。
     */
    private static final Set<String> ALLOWED_DIRECTIONS =
            Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("TOP", "BOTTOM", "LEFT", "RIGHT")));

    private final MonitorTaskManageMapper taskManageMapper;
    private final MonitorTaskDependencyManageMapper dependencyManageMapper;
    private final MonitorSystemManageMapper systemManageMapper;

    /**
     * 查询任务列表。
     */
    @Override
    public List<MonitorTaskDef> listTasks() {
        return taskManageMapper.selectAll();
    }

    /**
     * 查询任务详情及依赖关系。
     *
     * @param taskCode 任务编码
     */
    @Override
    public TaskManageDetailVO getTaskDetail(String taskCode) {
        TaskManageDetailVO detail = new TaskManageDetailVO();
        detail.setTask(taskManageMapper.selectByCode(taskCode));
        detail.setDependencies(toDependencyItems(dependencyManageMapper.selectByTaskCode(taskCode)));
        return detail;
    }

    /**
     * 新增任务主数据及依赖关系。
     *
     * @param request 任务新增请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTask(TaskSaveRequest request) {
        try {
            validateTaskRequest(request);
            if (taskManageMapper.selectByCode(request.getTaskCode()) != null) {
                throw new BusinessException("任务编码已存在: " + request.getTaskCode());
            }
            taskManageMapper.insert(toTaskEntity(request));
            replaceDependencies(request.getTaskCode(), request.getDependencies());
        } catch (RuntimeException ex) {
            log.error("新增任务主数据及依赖关系异常，taskCode={}", request.getTaskCode(), ex);
            throw ex;
        }
    }

    /**
     * 修改任务主数据及依赖关系。
     *
     * @param request 任务修改请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(TaskSaveRequest request) {
        try {
            validateTaskRequest(request);
            if (taskManageMapper.selectByCode(request.getTaskCode()) == null) {
                throw new BusinessException("任务不存在: " + request.getTaskCode());
            }
            taskManageMapper.updateByCode(toTaskEntity(request));
            replaceDependencies(request.getTaskCode(), request.getDependencies());
        } catch (RuntimeException ex) {
            log.error("修改任务主数据及依赖关系异常，taskCode={}", request.getTaskCode(), ex);
            throw ex;
        }
    }

    /**
     * 删除任务及其依赖关系。
     *
     * @param taskCode 任务编码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(String taskCode) {
        try {
            dependencyManageMapper.deleteRelatedByTaskCode(taskCode);
            taskManageMapper.deleteByCode(taskCode);
        } catch (RuntimeException ex) {
            log.error("删除任务及依赖关系异常，taskCode={}", taskCode, ex);
            throw ex;
        }
    }

    /**
     * 校验任务保存请求。
     *
     * @param request 任务保存请求参数
     */
    private void validateTaskRequest(TaskSaveRequest request) {
        if (systemManageMapper.selectByCode(request.getSystemCode()) == null) {
            throw new BusinessException("所属系统不存在: " + request.getSystemCode());
        }
        if (request.getPlanEndTime().isBefore(request.getPlanStartTime())) {
            throw new BusinessException("预计结束时间不能早于预计开始时间");
        }
        validateDependencies(request.getTaskCode(), request.getDependencies());
    }

    /**
     * 将任务保存请求转换为任务定义实体。
     *
     * @param request 任务保存请求参数
     */
    private MonitorTaskDef toTaskEntity(TaskSaveRequest request) {
        MonitorTaskDef entity = new MonitorTaskDef();
        entity.setTaskCode(request.getTaskCode());
        entity.setTaskName(request.getTaskName());
        entity.setSystemCode(request.getSystemCode());
        entity.setPreRequisiteProd(request.getPreRequisiteProd());
        entity.setTheBatchProd(request.getTheBatchProd());
        entity.setPlanStartTime(request.getPlanStartTime());
        entity.setPlanEndTime(request.getPlanEndTime());
        entity.setDefaultCostMinutes(defaultInt(request.getDefaultCostMinutes(), 0));
        entity.setBatchProcessing(defaultStr(request.getBatchProcessing(), "D"));
        entity.setIsPath(defaultStr(request.getIsPath(), "0"));
        entity.setPosX(defaultInt(request.getPosX(), 0));
        entity.setPosY(defaultInt(request.getPosY(), 0));
        entity.setIsFlag(defaultStr(request.getIsFlag(), "0"));
        entity.setRemark(request.getRemark());
        return entity;
    }

    /**
     * 替换任务依赖关系。
     *
     * @param taskCode 当前任务编码
     * @param dependencies 当前任务依赖列表
     */
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

    /**
     * 将依赖实体列表转换为依赖出参列表。
     *
     * @param entities 依赖实体列表
     */
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
     *
     * @param taskCode 当前任务编码
     * @param dependencies 当前任务依赖列表
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
     *
     * @param direction 原始箭头方向
     */
    private String defaultDirection(String direction) {
        return direction == null || direction.isBlank() ? "RIGHT" : direction.trim().toUpperCase();
    }

    /**
     * 为字符串字段补默认值。
     *
     * @param value 原始值
     * @param defaultValue 默认值
     */
    private String defaultStr(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    /**
     * 为整数字段补默认值。
     *
     * @param value 原始值
     * @param defaultValue 默认值
     */
    private Integer defaultInt(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }
}
