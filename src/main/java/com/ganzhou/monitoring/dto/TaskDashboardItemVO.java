package com.ganzhou.monitoring.dto;

import lombok.Data;

/**
 * Description: 大屏任务信息。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class TaskDashboardItemVO {

    /** 业务日期。 */
    private String bizDate;

    /** 重跑次数。 */
    private Integer runTimes;

    /** 任务编码。 */
    private String taskCode;

    /** 任务名称。 */
    private String taskName;

    /** 系统编码。 */
    private String systemCode;

    /** 系统名称。 */
    private String systemName;

    /** 前置依赖产物。 */
    private String preRequisiteProd;

    /** 本批处理任务产出物。 */
    private String theBatchProd;

    /** 横坐标。 */
    private Integer posX;

    /** 纵坐标。 */
    private Integer posY;

    /** 默认耗时，单位分钟。 */
    private Integer defaultCostMinutes;

    /** 跑批周期。 */
    private String batchProcessing;

    /** 是否关键路径。 */
    private String isPath;

    /** 任务启停状态。 */
    private String taskIsFlag;

    /** 任务备注。 */
    private String remark;

    /** 计划开始时间，格式 HH:mm:ss。 */
    private String planStartTime;

    /** 最晚开始时间，格式 HH:mm:ss。 */
    private String latestStartTime;

    /** 计划结束时间，格式 HH:mm:ss。 */
    private String planEndTime;

    /** 实际开始时间，格式 HH:mm:ss。 */
    private String actualStartTime;

    /** 实际结束时间，格式 HH:mm:ss。 */
    private String actualEndTime;

    /** 当前耗时，格式 mm:ss。 */
    private String currentCostMinutes;

    /** 历史平均耗时，格式 mm:ss。 */
    private String avgCostMinutes;

    /** 最晚结束时间，格式 HH:mm:ss。 */
    private String latestEndTime;

    /** 是否延迟。 */
    private Integer delayedFlag;

    /** 是否超时。 */
    private Integer timeoutFlag;

    /** 执行结果状态。 */
    private String resultStatus;

    /** 执行结果状态中文说明。 */
    private String resultStatusName;

    /** 是否展示。 */
    private String isFlag;

}
