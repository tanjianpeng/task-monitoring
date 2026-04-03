package com.ganzhou.monitoring.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.dto.TaskReportRequest;
import com.ganzhou.monitoring.entity.MonitorTaskDef;
import com.ganzhou.monitoring.entity.MonitorTaskEvent;
import com.ganzhou.monitoring.entity.MonitorTaskInstance;
import com.ganzhou.monitoring.mapper.MonitorTaskDefMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskEventMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskInstanceMapper;
import com.ganzhou.monitoring.service.MonitoringService;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    /**
     * 任务定义查询 Mapper。
     */
    private final MonitorTaskDefMapper taskDefMapper;

    /**
     * 任务实例读写 Mapper。
     */
    private final MonitorTaskInstanceMapper taskInstanceMapper;

    /**
     * 任务事件流水 Mapper。
     */
    private final MonitorTaskEventMapper taskEventMapper;

    /**
     * JSON 序列化工具。
     * 用于保存原始请求报文以及扩展字段。
     */
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer handleTaskAction(String action, TaskReportRequest request) {
        return switch (action) {
            case "begin" -> handleBegin(request);
            case "end" -> handleEnd(request);
            default -> throw new BusinessException("不支持的动作类型: " + action);
        };
    }

    /**
     * 处理开始动作。
     */
    private Integer handleBegin(TaskReportRequest request) {
        if (request.getStartTime() == null) {
            throw new BusinessException("begin动作时startTime不能为空");
        }
        MonitorTaskEvent event = buildLog("begin", request, "/api/monitor/task/begin");
        taskEventMapper.insert(event);

        MonitorTaskInstance instance = taskInstanceMapper.selectByBizDateAndTaskCodeAndRunNo(
                request.getBizDate().format(DateTimeFormatter.BASIC_ISO_DATE), request.getTaskCode(), request.getRunNo());
        if (instance == null) {
            instance = initInstance(request);
            taskInstanceMapper.insert(instance);
            instance = taskInstanceMapper.selectByBizDateAndTaskCodeAndRunNo(
                    request.getBizDate().format(DateTimeFormatter.BASIC_ISO_DATE), request.getTaskCode(), request.getRunNo());
        }
        applyBegin(instance, request);
        taskInstanceMapper.updateById(instance);
        return request.getRunNo();
    }

    /**
     * 处理结束动作。
     */
    private Integer handleEnd(TaskReportRequest request) {
        if (request.getEndTime() == null) {
            throw new BusinessException("end动作时endTime不能为空");
        }
        if (request.getResultStatus() == null || request.getResultStatus().isBlank()) {
            throw new BusinessException("end动作时resultStatus不能为空");
        }
        MonitorTaskEvent event = buildLog("end", request, "/api/monitor/task/end");
        taskEventMapper.insert(event);
        MonitorTaskInstance instance = getRequiredInstance(request);
        applyEnd(instance, request);
        taskInstanceMapper.updateById(instance);
        return request.getRunNo();
    }

    private MonitorTaskInstance getRequiredInstance(TaskReportRequest request) {
        MonitorTaskInstance instance = taskInstanceMapper.selectByBizDateAndTaskCodeAndRunNo(
                request.getBizDate().format(DateTimeFormatter.BASIC_ISO_DATE), request.getTaskCode(), request.getRunNo());
        if (instance == null) {
            throw new BusinessException("任务实例不存在，请先调用begin接口");
        }
        return instance;
    }

    /**
     * 将上报请求转换为调用日志对象。
     */
    private MonitorTaskEvent buildLog(String actionType, TaskReportRequest request, String requestUrl) {
        MonitorTaskEvent log = new MonitorTaskEvent();
        log.setId(UUID.randomUUID().toString().replace("-", ""));
        log.setRequestId(request.getRequestId());
        log.setRequestUrl(requestUrl);
        log.setActionType(actionType);
        log.setBizDate(request.getBizDate().format(DateTimeFormatter.BASIC_ISO_DATE));
        log.setRunTimes(request.getRunNo());
        log.setTaskCode(request.getTaskCode());
        log.setSystemCode(request.getSystemCode());
        log.setResultStatus(request.getResultStatus());
        log.setErrorMsg(request.getErrorMessage());
        log.setRequestJson(writeJson(request));
        return log;
    }

    /**
     * 基于任务定义初始化当天任务实例。
     * 主要用于兜底场景，例如调度未预生成实例、但业务系统已经开始上报。
     */
    private MonitorTaskInstance initInstance(TaskReportRequest request) {
        MonitorTaskDef taskDef = taskDefMapper.selectByTaskCode(request.getTaskCode());
        if (taskDef == null) {
            throw new BusinessException("任务定义不存在: " + request.getTaskCode());
        }

        MonitorTaskInstance instance = new MonitorTaskInstance();
        instance.setId(UUID.randomUUID().toString().replace("-", ""));
        instance.setBizDate(request.getBizDate().format(DateTimeFormatter.BASIC_ISO_DATE));
        instance.setRunTimes(request.getRunNo());
        instance.setTaskCode(taskDef.getTaskCode());
        instance.setSystemCode(taskDef.getSystemCode());
        instance.setPlanStartTime(taskDef.getPlanStartTime());
        instance.setPlanEndTime(taskDef.getPlanEndTime());
        instance.setLatestStartTime(instance.getPlanStartTime()
                .plusMinutes(taskDef.getDelayMinutes()));
        if (instance.getPlanStartTime() != null && taskDef.getAvgCostMinutes() != null) {
            instance.setPredictEndTime(instance.getPlanStartTime()
                    .plusMinutes(taskDef.getAvgCostMinutes()));
        }
        instance.setDelayedFlag(0);
        instance.setTimeoutFlag(0);
        instance.setIsFlag(0);
        return instance;
    }

    /**
     * 应用开始动作到实例快照。
     */
    private void applyBegin(MonitorTaskInstance instance, TaskReportRequest request) {
        instance.setActualStartTime(request.getStartTime());
        instance.setResultStatus("RUNNING");
        instance.setErrorMsg(null);
        if (instance.getPredictEndTime() == null && instance.getPlanEndTime() != null) {
            instance.setPredictEndTime(instance.getPlanEndTime());
        } else if (instance.getActualStartTime() != null && instance.getPredictEndTime() != null
                && instance.getPlanStartTime() != null) {
            long minutes = Duration.between(instance.getPlanStartTime(), instance.getPredictEndTime()).toMinutes();
            instance.setPredictEndTime(
                    instance.getActualStartTime().plusMinutes(minutes));
        }
        markFlags(instance);
    }

    /**
     * 应用结束动作到实例快照。
     */
    private void applyEnd(MonitorTaskInstance instance, TaskReportRequest request) {
        if (instance.getActualStartTime() == null && request.getStartTime() != null) {
            instance.setActualStartTime(request.getStartTime());
        }
        instance.setActualEndTime(request.getEndTime());
        fillCost(instance, request);
        if ("SUCCESS".equalsIgnoreCase(request.getResultStatus())) {
            instance.setResultStatus("SUCCESS");
            instance.setErrorMsg(null);
        } else if ("FAILED".equalsIgnoreCase(request.getResultStatus())) {
            instance.setResultStatus("FAILED");
            instance.setErrorMsg(request.getErrorMessage());
        } else {
            throw new BusinessException("end动作时resultStatus仅支持SUCCESS或FAILED");
        }
        markFlags(instance);
    }

    /**
     * 填充耗时。
     * 优先使用业务系统直接回传的耗时，其次再由开始时间和结束时间计算。
     */
    private void fillCost(MonitorTaskInstance instance, TaskReportRequest request) {
        if (request.getCostSeconds() != null) {
            instance.setCurrentCostMinutes(request.getCostSeconds());
            return;
        }
        if (instance.getActualStartTime() != null && instance.getActualEndTime() != null) {
            long minutes = Duration.between(instance.getActualStartTime(), instance.getActualEndTime()).toMinutes();
            instance.setCurrentCostMinutes((int) minutes);
        }
    }

    /**
     * 根据当前时间和实例信息判断是否延迟、是否超时。
     * 这段逻辑面向的是“当前态”，因此会在每次上报后刷新一次。
     */
    private void markFlags(MonitorTaskInstance instance) {
        LocalDateTime now = LocalDateTime.now();
        if (instance.getActualStartTime() == null
                && instance.getLatestStartTime() != null
                && now.isAfter(instance.getLatestStartTime())
                && (instance.getResultStatus() == null || instance.getResultStatus().isBlank())) {
            instance.setDelayedFlag(1);
        }
        if (instance.getActualStartTime() != null
                && instance.getActualEndTime() == null
                && instance.getPredictEndTime() != null
                && now.isAfter(instance.getPredictEndTime())) {
            instance.setTimeoutFlag(1);
        }
    }

    /**
     * 返回非空时间。
     */
    private LocalDateTime nvl(LocalDateTime first, LocalDateTime second) {
        return first != null ? first : second;
    }

    /**
     * 序列化对象为 JSON 字符串。
     * 主要用于保留请求快照，便于后续排障。
     */
    private String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return String.valueOf(value);
        }
    }
}
