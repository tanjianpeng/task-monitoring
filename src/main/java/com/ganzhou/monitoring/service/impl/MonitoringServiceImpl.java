package com.ganzhou.monitoring.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.constant.TaskResultStatusEnum;
import com.ganzhou.monitoring.dto.TaskReportRequest;
import com.ganzhou.monitoring.entity.MonitorTaskDef;
import com.ganzhou.monitoring.entity.MonitorTaskEvent;
import com.ganzhou.monitoring.entity.MonitorTaskInstance;
import com.ganzhou.monitoring.mapper.MonitorTaskDefMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskEventMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskInstanceMapper;
import com.ganzhou.monitoring.service.MonitoringService;
import com.ganzhou.monitoring.service.TaskRuntimeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 监控服务实现类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    /**
     * 外部系统统一上报地址。
     */
    private static final String REPORT_URL = "/api/monitor/task/report";

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

    /**
     * 任务运行配置查询服务。
     * 用于预留按 taskCode 查询允许延迟分钟数等动态配置。
     */
    private final TaskRuntimeConfigService taskRuntimeConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer handleTaskAction(String action, TaskReportRequest request) {
        String normalizedAction = normalizeAction(action, request);
        return switch (normalizedAction) {
            case "start" -> handleStart(request);
            case "stop" -> handleStop(request);
            case "restart" -> handleRestart(request);
            case "fail" -> handleFail(request);
            default -> throw new BusinessException("不支持的动作类型: " + normalizedAction);
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int initializeTaskInstances(LocalDate bizDate) {
        String bizDateText = formatBizDate(bizDate);
        List<MonitorTaskDef> taskDefs = taskDefMapper.selectActiveTasks();
        int affected = 0;
        for (MonitorTaskDef taskDef : taskDefs) {
            MonitorTaskInstance snapshot = buildInstanceSnapshot(taskDef, bizDate, 0);
            MonitorTaskInstance existing = taskInstanceMapper.selectByBizDateAndTaskCodeAndRunNo(
                    bizDateText, taskDef.getTaskCode(), 0);
            affected += saveOrRefreshInitializedInstance(snapshot, existing);
        }
        return affected;
    }

    /**
     * 处理开始动作。
     * 业务系统调用 start 时，只实时更新当前任务自己的实例数据。
     */
    private Integer handleStart(TaskReportRequest request) {
        LocalDateTime requestTime = resolveRequestTime(request);
        if (request.getStartTime() == null) {
            request.setStartTime(requestTime);
        }
        Integer runNo = normalizeRunNo(request);
        MonitorTaskDef taskDef = validateTask(request);
        MonitorTaskInstance instance = taskInstanceMapper.selectByBizDateAndTaskCodeAndRunNo(
                formatBizDate(request.getBizDate()), request.getTaskCode(), runNo);
        if (instance == null) {
            instance = initInstance(request, taskDef);
        } else {
            refreshPlanFields(instance, taskDef, request.getBizDate());
        }
        applyStart(instance, request, requestTime);
        MonitorTaskEvent event = buildLog("start", instance.getResultStatus(), request, REPORT_URL);
        taskEventMapper.insert(event);
        saveOrUpdateStartedInstance(instance);
        return runNo;
    }

    /**
     * 处理结束动作。
     * 业务系统调用 stop 时，只实时更新当前任务自己的实例数据，并写入成功状态。
     */
    private Integer handleStop(TaskReportRequest request) {
        LocalDateTime requestTime = resolveRequestTime(request);
        if (request.getEndTime() == null) {
            request.setEndTime(requestTime);
        }
        Integer runNo = normalizeRunNo(request);
        MonitorTaskDef taskDef = validateTask(request);
        MonitorTaskInstance instance = getRequiredInstance(request);
        applyStop(instance, taskDef, TaskResultStatusEnum.SUCCESS, request, requestTime);
        MonitorTaskEvent event = buildLog("stop", instance.getResultStatus(), request, REPORT_URL);
        taskEventMapper.insert(event);
        taskInstanceMapper.updateById(instance);
        return runNo;
    }

    /**
     * 处理失败动作。
     * fail 表示任务执行失败结束，未显式传结束时间时默认使用请求时间。
     * 业务系统调用 fail 时，只实时更新当前任务自己的实例数据，并写入失败状态。
     */
    private Integer handleFail(TaskReportRequest request) {
        LocalDateTime requestTime = resolveRequestTime(request);
        if (request.getEndTime() == null) {
            request.setEndTime(requestTime);
        }
        Integer runNo = normalizeRunNo(request);
        MonitorTaskDef taskDef = validateTask(request);
        MonitorTaskInstance instance = getRequiredInstance(request);
        applyStop(instance, taskDef, TaskResultStatusEnum.FAILED, request, requestTime);
        MonitorTaskEvent event = buildLog("fail", instance.getResultStatus(), request, REPORT_URL);
        taskEventMapper.insert(event);
        taskInstanceMapper.updateById(instance);
        return runNo;
    }

    /**
     * 处理重跑动作。
     * 重跑会自动生成新的批次号，并将实例状态重置为新一轮运行中。
     */
    private Integer handleRestart(TaskReportRequest request) {
        LocalDateTime requestTime = resolveRequestTime(request);
        if (request.getStartTime() == null) {
            request.setStartTime(requestTime);
        }
        MonitorTaskDef taskDef = validateTask(request);
        Integer runNo = nextRunNo(request);
        request.setRunNo(runNo);
        MonitorTaskInstance instance = initInstance(request, taskDef);
        applyStart(instance, request, requestTime);
        MonitorTaskEvent event = buildLog("restart", instance.getResultStatus(), request, REPORT_URL);
        taskEventMapper.insert(event);
        taskInstanceMapper.insert(instance);
        return runNo;
    }

    private MonitorTaskInstance getRequiredInstance(TaskReportRequest request) {
        MonitorTaskInstance instance = taskInstanceMapper.selectByBizDateAndTaskCodeAndRunNo(
                formatBizDate(request.getBizDate()), request.getTaskCode(), normalizeRunNo(request));
        if (instance == null) {
            throw new BusinessException("任务实例不存在，请先调用start或restart接口");
        }
        return instance;
    }

    /**
     * 校验任务定义是否存在，且任务与上报系统编码匹配。
     */
    private MonitorTaskDef validateTask(TaskReportRequest request) {
        MonitorTaskDef taskDef = taskDefMapper.selectByTaskCode(request.getTaskCode());
        if (taskDef == null) {
            throw new BusinessException("任务定义不存在: " + request.getTaskCode());
        }
        if (!taskDef.getSystemCode().equals(request.getSystemCode())) {
            throw new BusinessException("任务编码与系统编码不匹配");
        }
        return taskDef;
    }

    /**
     * 将上报请求转换为调用日志对象。
     */
    private MonitorTaskEvent buildLog(String actionType, String computedResultStatus,
                                      TaskReportRequest request, String requestUrl) {
        MonitorTaskEvent log = new MonitorTaskEvent();
        log.setId(UUID.randomUUID().toString().replace("-", ""));
        log.setRequestId(request.getRequestId());
        log.setRequestUrl(requestUrl);
        log.setRequestStatus(actionType);
        log.setBizDate(formatBizDate(request.getBizDate()));
        log.setRunTimes(normalizeRunNo(request));
        log.setTaskCode(request.getTaskCode());
        log.setSystemCode(request.getSystemCode());
        log.setResultStatus(computedResultStatus);
        log.setRequestJson(writeJson(request));
        return log;
    }

    /**
     * 基于任务定义初始化当天任务实例。
     * 主要用于兜底场景，例如调度未预生成实例、但业务系统已经开始上报。
     */
    private MonitorTaskInstance initInstance(TaskReportRequest request, MonitorTaskDef taskDef) {
        return buildInstanceSnapshot(taskDef, request.getBizDate(), normalizeRunNo(request));
    }

    /**
     * 将任务定义转换成某个业务日期下的实例快照。
     * 任务定义里保存的是模板时间，这里统一折算到具体业务日期。
     * 每天凌晨初始化实例时，默认状态写成 NOTSTART，表示任务尚未开始。
     */
    private MonitorTaskInstance buildInstanceSnapshot(MonitorTaskDef taskDef, LocalDate bizDate, Integer runNo) {
        MonitorTaskInstance instance = new MonitorTaskInstance();
        instance.setId(UUID.randomUUID().toString().replace("-", ""));
        instance.setBizDate(formatBizDate(bizDate));
        instance.setRunTimes(runNo);
        instance.setTaskCode(taskDef.getTaskCode());
        instance.setSystemCode(taskDef.getSystemCode());
        instance.setPlanStartTime(resolvePlanDateTime(bizDate, taskDef.getPlanStartTime()));
        instance.setPlanEndTime(resolvePlanDateTime(bizDate, taskDef.getPlanEndTime()));
        instance.setLatestStartTime(resolveLatestStartTime(taskDef, instance.getPlanStartTime()));
        instance.setCurrentCostMinutes(0);
        instance.setAvgCostMinutes(resolveHistoricalAvgCostMinutes(taskDef.getTaskCode(), bizDate, taskDef));
        instance.setFrequency(resolveInstanceFrequency(bizDate));
        instance.setPredictEndTime(null);
        instance.setDelayedFlag(0);
        instance.setTimeoutFlag(0);
        instance.setResultStatus(TaskResultStatusEnum.NOTSTART.getCode());
        instance.setIsFlag(defaultDisplayFlag(taskDef.getIsFlag()));
        return instance;
    }

    /**
     * 应用开始动作到实例快照。
     * 若实际开始时间超过最晚开始时间，则当前状态记为延迟；
     * 预计结束时间统一按“开始时间 + 历史平均耗时”计算。
     */
    private void applyStart(MonitorTaskInstance instance, TaskReportRequest request, LocalDateTime requestTime) {
        instance.setActualStartTime(request.getStartTime());
        instance.setActualEndTime(null);
        instance.setCurrentCostMinutes(0);
        instance.setResultStatus(TaskResultStatusEnum.RUNNING.getCode());
        if (instance.getLatestStartTime() != null
                && request.getStartTime() != null
                && request.getStartTime().isAfter(instance.getLatestStartTime())) {
            instance.setDelayedFlag(1);
            instance.setResultStatus(TaskResultStatusEnum.DELAYED.getCode());
        } else {
            instance.setDelayedFlag(0);
        }
        refreshPredictEndTime(instance, request.getBizDate());
        markFlags(instance, requestTime);
    }

    /**
     * 应用结束动作到实例快照。
     * 如果结束回调到达时间或实际结束时间晚于任务预计结束时间，则记为延迟结束。
     */
    private void applyStop(MonitorTaskInstance instance, MonitorTaskDef taskDef, TaskResultStatusEnum computedResultStatus,
                           TaskReportRequest request, LocalDateTime requestTime) {
        if (instance.getActualStartTime() == null && request.getStartTime() != null) {
            instance.setActualStartTime(request.getStartTime());
        }
        refreshPlanFields(instance, taskDef, request.getBizDate());
        refreshPredictEndTime(instance, request.getBizDate());
        instance.setActualEndTime(request.getEndTime());
        fillCost(instance, request);
        if (TaskResultStatusEnum.SUCCESS == computedResultStatus) {
            instance.setResultStatus(TaskResultStatusEnum.SUCCESS.getCode());
        } else if (TaskResultStatusEnum.FAILED == computedResultStatus) {
            instance.setResultStatus(TaskResultStatusEnum.FAILED.getCode());
        } else {
            throw new BusinessException("结束动作的结果状态仅支持SUCCESS或FAILED");
        }
        if (instance.getActualEndTime() != null
                && instance.getPredictEndTime() != null
                && instance.getActualEndTime().isAfter(instance.getPredictEndTime())) {
            instance.setDelayedFlag(1);
            instance.setTimeoutFlag(1);
        }
        markFlags(instance, requestTime);
    }

    /**
     * 填充耗时。
     * 当前耗时统一按“结束调用时间 - 开始调用时间”计算。
     * 运行中或未开始场景不回写耗时，保持为 0。
     */
    private void fillCost(MonitorTaskInstance instance, TaskReportRequest request) {
        if (instance.getActualStartTime() != null && instance.getActualEndTime() != null) {
            long minutes = Duration.between(instance.getActualStartTime(), instance.getActualEndTime()).toMinutes();
            instance.setCurrentCostMinutes((int) minutes);
            return;
        }
        if (instance.getActualStartTime() != null) {
            instance.setCurrentCostMinutes(0);
        }
    }

    /**
     * 根据当前时间和实例信息判断是否延迟、是否超时。
     * 这段逻辑面向的是“当前态”，因此会在每次上报后刷新一次。
     * 状态口径说明：
     * 1. 外部未回传开始信息：NOTSTART。
     * 2. 已开始且开始时间未超过最晚开始时间，且尚未超过预计结束时间：RUNNING。
     * 3. 已开始但超过最晚开始时间，或已开始未结束且超过预计结束时间：DELAYED。
     * 4. 成功结束：SUCCESS。
     * 5. 失败结束：FAILED。
     * 6. 成功或失败结束后若超过预计结束时间，仅保留成功/失败状态，同时 delayedFlag 置为 1。
     */
    private void markFlags(MonitorTaskInstance instance, LocalDateTime requestTime) {
        LocalDateTime current = requestTime == null ? LocalDateTime.now() : requestTime;
        if (instance.getActualEndTime() != null) {
            instance.setCurrentCostMinutes(resolveCurrentCostMinutes(instance, current));
        } else if (instance.getActualStartTime() == null) {
            instance.setCurrentCostMinutes(0);
        }
        if (instance.getDelayedFlag() == null) {
            instance.setDelayedFlag(0);
        }
        instance.setTimeoutFlag(0);
        if (instance.getActualStartTime() == null) {
            instance.setDelayedFlag(0);
            instance.setResultStatus(TaskResultStatusEnum.NOTSTART.getCode());
            return;
        }
        if (instance.getActualEndTime() == null
                && instance.getPredictEndTime() != null
                && current.isAfter(instance.getPredictEndTime())) {
            instance.setTimeoutFlag(1);
            instance.setDelayedFlag(1);
            instance.setResultStatus(TaskResultStatusEnum.DELAYED.getCode());
            return;
        }
        if (instance.getActualEndTime() == null
                && instance.getLatestStartTime() != null
                && instance.getActualStartTime().isAfter(instance.getLatestStartTime())) {
            instance.setDelayedFlag(1);
            instance.setResultStatus(TaskResultStatusEnum.DELAYED.getCode());
            return;
        }
        if (instance.getActualEndTime() == null) {
            instance.setResultStatus(TaskResultStatusEnum.RUNNING.getCode());
            return;
        }
        if (instance.getActualEndTime() != null
                && instance.getPredictEndTime() != null
                && instance.getActualEndTime().isAfter(instance.getPredictEndTime())) {
            instance.setTimeoutFlag(1);
            instance.setDelayedFlag(1);
        }
    }

    /**
     * 根据开始时间和历史平均耗时刷新预计结束时间。
     * 平日视图取往前 30 个非月底业务日平均耗时，月底视图取往前 6 个自然月底业务日平均耗时。
     * 若统计区间内没有成功数据，则回退到任务定义中的 defaultCostMinutes。
     */
    private void refreshPredictEndTime(MonitorTaskInstance instance, LocalDate bizDate) {
        if (instance.getActualStartTime() == null) {
            instance.setPredictEndTime(null);
            return;
        }
        MonitorTaskDef taskDef = taskDefMapper.selectByTaskCode(instance.getTaskCode());
        Integer avgCostMinutes = resolveHistoricalAvgCostMinutes(instance.getTaskCode(), bizDate, taskDef);
        instance.setAvgCostMinutes(avgCostMinutes);
        if (avgCostMinutes == null || avgCostMinutes <= 0) {
            instance.setPredictEndTime(null);
            return;
        }
        instance.setPredictEndTime(instance.getActualStartTime().plusMinutes(avgCostMinutes));
    }

    /**
     * 计算实例最晚开始时间。
     * 允许延迟分钟数统一通过预留的动态配置查询能力按 taskCode 获取，未配置时回退为 0 分钟。
     */
    private LocalDateTime resolveLatestStartTime(MonitorTaskDef taskDef, LocalDateTime planStartTime) {
        if (planStartTime == null) {
            return null;
        }
        Integer allowDelayMinutes = resolveAllowDelayMinutes(taskDef);
        if (allowDelayMinutes == null) {
            return planStartTime;
        }
        return planStartTime.plusMinutes(allowDelayMinutes);
    }

    /**
     * 刷新实例中的计划类字段。
     * 当实例已提前生成、但动态配置在业务上报前发生变化时，确保延迟判定仍使用最新口径。
     */
    private void refreshPlanFields(MonitorTaskInstance instance, MonitorTaskDef taskDef, LocalDate bizDate) {
        instance.setSystemCode(taskDef.getSystemCode());
        instance.setPlanStartTime(resolvePlanDateTime(bizDate, taskDef.getPlanStartTime()));
        instance.setPlanEndTime(resolvePlanDateTime(bizDate, taskDef.getPlanEndTime()));
        instance.setLatestStartTime(resolveLatestStartTime(taskDef, instance.getPlanStartTime()));
        instance.setAvgCostMinutes(resolveHistoricalAvgCostMinutes(taskDef.getTaskCode(), bizDate, taskDef));
        instance.setFrequency(resolveInstanceFrequency(bizDate));
        if (instance.getActualStartTime() == null) {
            instance.setPredictEndTime(null);
        } else {
            refreshPredictEndTime(instance, bizDate);
        }
        instance.setIsFlag(defaultDisplayFlag(taskDef.getIsFlag()));
    }

    /**
     * 保存或刷新 start 动作对应的任务实例。
     * 先判断实例是否已存在，不存在时执行新增，存在时执行更新，避免直接 update 导致实例不存在时报错。
     */
    private void saveOrUpdateStartedInstance(MonitorTaskInstance instance) {
        MonitorTaskInstance existing = taskInstanceMapper.selectByBizDateAndTaskCodeAndRunNo(
                instance.getBizDate(), instance.getTaskCode(), instance.getRunTimes());
        if (existing == null) {
            taskInstanceMapper.insert(instance);
            return;
        }
        instance.setId(existing.getId());
        taskInstanceMapper.updateById(instance);
    }

    /**
     * 保存或刷新凌晨初始化生成的任务实例。
     * 若当天实例不存在则新增；若已存在但尚未开始执行，则仅更新计划类字段。
     */
    private int saveOrRefreshInitializedInstance(MonitorTaskInstance snapshot, MonitorTaskInstance existing) {
        if (existing == null) {
            return taskInstanceMapper.insert(snapshot);
        }
        if (existing.getActualStartTime() != null || existing.getActualEndTime() != null) {
            return 0;
        }
        snapshot.setId(existing.getId());
        return taskInstanceMapper.updatePlanFieldsById(snapshot);
    }

    /**
     * 统一处理是否展示默认值。
     * 任务表和实例表的 is_flag 默认都按 DDL 约定写入 "0"。
     */
    private String defaultDisplayFlag(String isFlag) {
        return isFlag == null || isFlag.isBlank() ? "0" : isFlag;
    }

    /**
     * 计算任务允许延迟分钟数。
     * 真实项目中统一由字典表或其他配置中心按 taskCode 查询，当前预留实现未查到时默认按 0 分钟处理。
     */
    private Integer resolveAllowDelayMinutes(MonitorTaskDef taskDef) {
        Integer configured = taskRuntimeConfigService.queryAllowDelayMinutesByTaskCode(taskDef.getTaskCode());
        if (configured != null && configured >= 0) {
            return configured;
        }
        return 0;
    }

    /**
     * 将任务定义中的时间模板折算到指定业务日期。
     * 如果任务定义本身已经带了目标日期，则直接保留时分秒并覆盖到业务日期上。
     */
    private LocalDateTime resolvePlanDateTime(LocalDate bizDate, LocalDateTime template) {
        if (bizDate == null || template == null) {
            return template;
        }
        LocalTime time = template.toLocalTime();
        return LocalDateTime.of(bizDate, time);
    }

    /**
     * 格式化业务日期，统一使用 yyyyMMdd。
     */
    private String formatBizDate(LocalDate bizDate) {
        return bizDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     * 将空批次号统一折算为 0，避免空值影响实例查询、日志记录和定时初始化逻辑。
     */
    private Integer normalizeRunNo(TaskReportRequest request) {
        return request.getRunNo() == null ? 0 : request.getRunNo();
    }

    /**
     * 统一归一化动作类型。
     * 兼容旧版 begin/end，也支持新版 start/stop/restart/fail。
     * 其中 stop 固定表示成功结束，fail 固定表示失败结束。
     */
    private String normalizeAction(String action, TaskReportRequest request) {
        String raw = request.getStatus() == null || request.getStatus().isBlank() ? action : request.getStatus();
        if (raw == null || raw.isBlank()) {
            throw new BusinessException("任务动作不能为空");
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "begin" -> "start";
            case "end" -> "stop";
            case "start", "stop", "restart", "fail" -> normalized;
            default -> normalized;
        };
    }

    /**
     * 解析本次调用的请求时间。
     * 业务方未显式传值时，回退到服务端接收时间。
     */
    private LocalDateTime resolveRequestTime(TaskReportRequest request) {
        return request.getRequestTime() != null ? request.getRequestTime() : LocalDateTime.now();
    }

    /**
     * 生成新的重跑次数。
     * 若当天已有历史实例，则在最大 run_times 基础上加一。
     */
    private Integer nextRunNo(TaskReportRequest request) {
        Integer maxRunTimes = taskInstanceMapper.selectMaxRunTimes(
                formatBizDate(request.getBizDate()), request.getTaskCode());
        return maxRunTimes == null ? 1 : maxRunTimes + 1;
    }

    /**
     * 计算当前耗时。
     * 未开始返回 0，执行中返回开始到当前的分钟差，已结束返回开始到结束的分钟差。
     */
    private Integer resolveCurrentCostMinutes(MonitorTaskInstance instance, LocalDateTime current) {
        if (instance.getActualStartTime() == null) {
            return 0;
        }
        LocalDateTime endTime = instance.getActualEndTime() == null ? null : instance.getActualEndTime();
        if (endTime == null) {
            return 0;
        }
        long minutes = Duration.between(instance.getActualStartTime(), endTime).toMinutes();
        return (int) Math.max(minutes, 0);
    }

    /**
     * 解析实例频率。
     * 由于实例表是一条任务 + 一条业务日期的数据，因此月底业务日期统一记为 M，平日统一记为 D。
     */
    private String resolveInstanceFrequency(LocalDate bizDate) {
        return isMonthEndView(bizDate) ? "M" : "D";
    }

    /**
     * 计算历史平均耗时。
     * 平日统计往前 30 个非月底业务日成功实例平均耗时；
     * 月底统计往前 6 个自然月底业务日成功实例平均耗时。
     */
    private Integer resolveHistoricalAvgCostMinutes(String taskCode, LocalDate bizDate, MonitorTaskDef taskDef) {
        LocalDate endDate = bizDate == null ? LocalDate.now() : bizDate;
        List<String> bizDateList = isMonthEndView(endDate)
                ? buildPreviousMonthEndBizDates(endDate, 6)
                : buildPreviousDailyBizDates(endDate, 30);
        Integer avgCostMinutes = bizDateList.isEmpty()
                ? null
                : taskInstanceMapper.selectHistoricalAvgCostMinutesByBizDateList(taskCode, bizDateList);
        if (avgCostMinutes != null && avgCostMinutes > 0) {
            return avgCostMinutes;
        }
        return taskDef == null ? null : taskDef.getDefaultCostMinutes();
    }

    /**
     * 判断是否为月底视图。
     * 业务日期为当月最后一天时，按往前 6 个自然月底统计历史平均耗时；否则按往前 30 个非月底业务日统计。
     */
    private boolean isMonthEndView(LocalDate bizDate) {
        if (bizDate == null) {
            return false;
        }
        return bizDate.equals(bizDate.with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * 构造平日场景历史平均耗时统计日期列表。
     * 从当前业务日期往前取 30 个非月底业务日，不包含当前业务日期本身。
     */
    private List<String> buildPreviousDailyBizDates(LocalDate bizDate, int size) {
        List<String> result = new java.util.ArrayList<>();
        LocalDate cursor = bizDate.minusDays(1);
        while (result.size() < size) {
            if (!isMonthEndView(cursor)) {
                result.add(formatBizDate(cursor));
            }
            cursor = cursor.minusDays(1);
        }
        return result;
    }

    /**
     * 构造月底场景历史平均耗时统计日期列表。
     * 从当前业务日期往前取 6 个自然月底业务日，不包含当前业务日期本身。
     */
    private List<String> buildPreviousMonthEndBizDates(LocalDate bizDate, int size) {
        List<String> result = new java.util.ArrayList<>();
        LocalDate cursor = bizDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        while (result.size() < size) {
            result.add(formatBizDate(cursor));
            cursor = cursor.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }
        return result;
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
