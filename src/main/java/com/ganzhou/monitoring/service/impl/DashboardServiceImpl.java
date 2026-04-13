package com.ganzhou.monitoring.service.impl;

import com.ganzhou.monitoring.dto.*;
import com.ganzhou.monitoring.mapper.MonitorDashboardMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskInstanceMapper;
import com.ganzhou.monitoring.service.DashboardService;
import com.ganzhou.monitoring.constant.TaskResultStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Description: 大屏服务实现类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    /**
     * 卡片时间展示格式。
     */
    private static final DateTimeFormatter CARD_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 大屏相关聚合查询 Mapper。
     */
    private final MonitorDashboardMapper dashboardMapper;

    /**
     * 任务实例查询 Mapper。
     * 用于读取最新业务日期以及按新口径统计历史平均耗时。
     */
    private final MonitorTaskInstanceMapper taskInstanceMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getStatistics() {
        try {
            LocalDate bizDate = resolveDashboardBizDate();
            String formatBizDate = formatBizDate(bizDate);
            SummaryVO summary = dashboardMapper.selectSummary(formatBizDate, isMonthEndView(bizDate));
            return buildStatistics(summary);
        } catch (RuntimeException ex) {
            log.error("查询本地大屏顶部统计异常", ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemDashboardCardVO> getSystems(String systemName) {
        try {
            LocalDate bizDate = resolveDashboardBizDate();
            return dashboardMapper.selectSystemCards(formatBizDate(bizDate), systemName, isMonthEndView(bizDate));
        } catch (RuntimeException ex) {
            log.error("查询本地大屏系统卡片异常，systemName={}", systemName, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TaskStatusDataVO getTaskStatusList() {
        try {
            LocalDate bizDate = resolveDashboardBizDate();
            String formatBizDate = formatBizDate(bizDate);
            boolean monthEndView = isMonthEndView(bizDate);
            List<TaskCardVO> taskCards = dashboardMapper.selectTaskCards(formatBizDate, monthEndView);
            List<LinkVO> links = dashboardMapper.selectLinks(monthEndView);
            TaskStatusDataVO result = new TaskStatusDataVO();
            result.setTaskList(buildTaskItems(bizDate, taskCards, links));
            result.setDependencyNodeList(links);
            return result;
        } catch (RuntimeException ex) {
            log.error("查询本地大屏任务状态异常", ex);
            throw ex;
        }
    }

    private String formatBizDate(LocalDate bizDate) {
        return bizDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     * 将顶部汇总对象转成前端需要的字符串对象。
     * 返回字段统一使用稳定英文 key，便于前端按固定字段名取值渲染。
     *
     * @param summary 顶部统计汇总对象
     */
    private Map<String, String> buildStatistics(SummaryVO summary) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("systemCount", String.valueOf(defaultInt(summary.getSystemCount())));
        result.put("taskCount", String.valueOf(defaultInt(summary.getTaskCount())));
        result.put("notStartCount", String.valueOf(defaultInt(summary.getNotStartCount())));
        result.put("runningCount", String.valueOf(defaultInt(summary.getRunningCount())));
        result.put("successCount", String.valueOf(defaultInt(summary.getSuccessCount())));
        result.put("failedCount", String.valueOf(defaultInt(summary.getFailedCount())));
        return result;
    }

    /**
     * 将任务卡片组装成任务列表。
     * 任务节点和依赖连线已拆分成两个 list，方便前端分别渲染节点和箭头。
     *
     * @param bizDate 当前查询使用的业务日期
     * @param taskCards 任务卡片原始数据列表
     * @param links 任务依赖连线列表
     */
    private List<TaskDashboardItemVO> buildTaskItems(LocalDate bizDate, List<TaskCardVO> taskCards, List<LinkVO> links) {
        Map<String, TaskDashboardItemVO> taskItemMap = new LinkedHashMap<>();
        for (TaskCardVO card : taskCards) {
            TaskDashboardItemVO item = new TaskDashboardItemVO();
            item.setBizDate(card.getBizDate());
            item.setRunTimes(card.getRunTimes());
            item.setTaskCode(card.getTaskCode());
            item.setTaskName(card.getTaskName());
            item.setSystemCode(card.getSystemCode());
            item.setSystemName(card.getSystemName());
            item.setPreRequisiteProd(card.getPreRequisiteProd());
            item.setTheBatchProd(card.getTheBatchProd());
            item.setPosX(card.getPosX());
            item.setPosY(card.getPosY());
            item.setDefaultCostMinutes(card.getDefaultCostMinutes());
            item.setBatchProcessing(card.getBatchProcessing());
            item.setIsPath(card.getIsPath());
            item.setTaskIsFlag(card.getTaskIsFlag());
            item.setRemark(card.getRemark());
            item.setPlanStartTime(card.getPlanStartTime());
            item.setLatestStartTime(card.getLatestStartTime());
            item.setPlanEndTime(card.getPlanEndTime());
            item.setActualStartTime(formatClockTime(card.getActualStartTime()));
            item.setActualEndTime(formatClockTime(card.getActualEndTime()));
            item.setCurrentCostMinutes(formatCurrentCost(card));
            item.setAvgCostMinutes(formatAverageCost(bizDate, card.getTaskCode(), card.getDefaultCostMinutes()));
            item.setLatestEndTime(card.getLatestEndTime());
            item.setDelayedFlag(card.getDelayedFlag());
            item.setTimeoutFlag(card.getTimeoutFlag());
            item.setResultStatus(card.getResultStatus());
            item.setResultStatusName(resolveResultStatusName(card.getResultStatus()));
            item.setIsFlag(card.getIsFlag());
            taskItemMap.put(card.getTaskCode(), item);
        }
        return sortTaskItemsByDependency(taskCards, taskItemMap, links);
    }

    /**
     * 解析本地大屏查询使用的业务日期。
     * 优先使用实例表最新业务日期，实例表为空时回退到昨天日期。
     */
    private LocalDate resolveDashboardBizDate() {
        String latestBizDate = taskInstanceMapper.selectLatestBizDate();
        if (latestBizDate == null || latestBizDate.isBlank()) {
            return LocalDate.now().minusDays(1);
        }
        return LocalDate.parse(latestBizDate, DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     * 将时间格式化成任务卡片需要的 HH:mm:ss。
     *
     * @param value 原始时间
     */
    private String formatClockTime(LocalDateTime value) {
        return value == null ? null : value.format(CARD_TIME_FORMATTER);
    }

    /**
     * 将秒数格式化成任务卡片需要的 mm:ss。
     *
     * @param seconds 秒数
     */
    private String formatCostSeconds(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return "00:00";
        }
        long safeSeconds = Math.max(seconds, 0);
        Duration duration = Duration.ofSeconds(safeSeconds);
        long minutes = duration.toMinutes();
        long remainSeconds = duration.minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d", minutes, remainSeconds);
    }

    /**
     * 格式化任务当前耗时。
     * 待执行任务没有耗时，直接返回空值；其余场景按实际秒数转成 mm:ss。
     *
     * @param card 任务卡片原始数据
     */
    private String formatCurrentCost(TaskCardVO card) {
        if (card.getActualStartTime() == null) {
            return null;
        }
        return formatCostSeconds(card.getCurrentCostSeconds());
    }

    /**
     * 按当前业务日期口径格式化历史平均耗时。
     * 平日统计近 30 个非月底业务日，月底统计往前 6 个自然月底业务日。
     *
     * @param bizDate 当前大屏业务日期
     * @param taskCode 任务编码
     * @param defaultCostMinutes 默认耗时分钟数
     */
    private String formatAverageCost(LocalDate bizDate, String taskCode, Integer defaultCostMinutes) {
        List<String> bizDateList = isMonthEndView(bizDate)
                ? buildPreviousMonthEndBizDates(bizDate, 6)
                : buildPreviousDailyBizDates(bizDate, 30);
        Integer avgCostMinutes = null;
        if (!bizDateList.isEmpty()) {
            avgCostMinutes = taskInstanceMapper.selectHistoricalAvgCostMinutesByBizDateList(taskCode, bizDateList);
        }
        if (avgCostMinutes != null && avgCostMinutes > 0) {
            return formatCostSeconds(avgCostMinutes * 60);
        }
        if (defaultCostMinutes != null && defaultCostMinutes > 0) {
            return formatCostSeconds(defaultCostMinutes * 60);
        }
        return null;
    }

    /**
     * 将结果状态码转成中文说明，便于前端悬浮弹框直接展示状态徽标。
     *
     * @param resultStatus 任务结果状态编码
     */
    private String resolveResultStatusName(String resultStatus) {
        if (resultStatus == null || resultStatus.isBlank()) {
            return null;
        }
        for (TaskResultStatusEnum item : TaskResultStatusEnum.values()) {
            if (item.getCode().equalsIgnoreCase(resultStatus)) {
                return item.getDescription();
            }
        }
        return resultStatus;
    }

    /**
     * 构造平日场景历史平均耗时统计日期列表。
     * 从当前业务日期往前取 30 个非月底业务日，不包含当前业务日期本身。
     *
     * @param bizDate 当前业务日期
     * @param size 需要回溯的业务日数量
     */
    private List<String> buildPreviousDailyBizDates(LocalDate bizDate, int size) {
        List<String> result = new ArrayList<>();
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
     *
     * @param bizDate 当前业务日期
     * @param size 需要回溯的月底数量
     */
    private List<String> buildPreviousMonthEndBizDates(LocalDate bizDate, int size) {
        List<String> result = new ArrayList<>();
        LocalDate cursor = bizDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        while (result.size() < size) {
            result.add(formatBizDate(cursor));
            cursor = cursor.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }
        return result;
    }

    /**
     * 判断是否为月底视图。
     * 业务日期为当月最后一天时返回 true；传空值时返回 false，避免空指针。
     *
     * @param bizDate 业务日期
     */
    private boolean isMonthEndView(LocalDate bizDate) {
        if (bizDate == null) {
            return false;
        }
        return bizDate.equals(bizDate.with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * 按依赖关系对任务列表做拓扑排序。
     * 这样前端收到的 list 会尽量按“前置任务在前，末尾任务在后”的顺序排列。
     * 若依赖配置中存在环，则保留剩余任务的原始顺序追加到末尾。
     *
     * @param taskCards 任务卡片原始数据列表
     * @param taskItemMap 任务编码与任务出参的映射
     * @param links 任务依赖连线列表
     */
    private List<TaskDashboardItemVO> sortTaskItemsByDependency(List<TaskCardVO> taskCards,
                                                                Map<String, TaskDashboardItemVO> taskItemMap,
                                                                List<LinkVO> links) {
        Map<String, Integer> originalOrder = new HashMap<>();
        for (int index = 0; index < taskCards.size(); index++) {
            originalOrder.put(taskCards.get(index).getTaskCode(), index);
        }

        Map<String, Integer> inDegreeMap = new HashMap<>();
        Map<String, List<String>> nextTaskMap = new HashMap<>();
        for (String taskCode : taskItemMap.keySet()) {
            inDegreeMap.put(taskCode, 0);
            nextTaskMap.put(taskCode, new ArrayList<>());
        }

        for (LinkVO link : links) {
            String fromTaskCode = link.getPreTaskCode();
            String toTaskCode = link.getTaskCode();
            if (!taskItemMap.containsKey(fromTaskCode) || !taskItemMap.containsKey(toTaskCode)) {
                continue;
            }
            nextTaskMap.get(fromTaskCode).add(toTaskCode);
            inDegreeMap.put(toTaskCode, inDegreeMap.get(toTaskCode) + 1);
        }

        List<String> zeroInDegreeTasks = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : inDegreeMap.entrySet()) {
            if (entry.getValue() == 0) {
                zeroInDegreeTasks.add(entry.getKey());
            }
        }
        zeroInDegreeTasks.sort(Comparator.comparingInt(taskCode -> originalOrder.getOrDefault(taskCode, Integer.MAX_VALUE)));

        Queue<String> queue = new ArrayDeque<>(zeroInDegreeTasks);
        List<TaskDashboardItemVO> sortedItems = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            String currentTaskCode = queue.poll();
            if (!visited.add(currentTaskCode)) {
                continue;
            }
            sortedItems.add(taskItemMap.get(currentTaskCode));

            List<String> nextTaskCodes = nextTaskMap.getOrDefault(currentTaskCode, Collections.emptyList());
            nextTaskCodes.sort(Comparator.comparingInt(taskCode -> originalOrder.getOrDefault(taskCode, Integer.MAX_VALUE)));
            for (String nextTaskCode : nextTaskCodes) {
                int currentInDegree = inDegreeMap.getOrDefault(nextTaskCode, 0) - 1;
                inDegreeMap.put(nextTaskCode, currentInDegree);
                if (currentInDegree == 0) {
                    queue.offer(nextTaskCode);
                }
            }
        }

        for (TaskCardVO card : taskCards) {
            if (!visited.contains(card.getTaskCode())) {
                sortedItems.add(taskItemMap.get(card.getTaskCode()));
            }
        }
        return sortedItems;
    }

    /**
     * 整数空值兜底为 0。
     *
     * @param value 原始整数值
     */
    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }
}
