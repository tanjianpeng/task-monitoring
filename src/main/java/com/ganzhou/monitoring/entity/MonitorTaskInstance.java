package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MonitorTaskInstance {

    /** 主键ID。 */
    private String id;
    /** 业务日期。 */
    private String bizDate;
    /** 重跑次数。 */
    private Integer runTimes;
    /** 任务编码。 */
    private String taskCode;
    /** 系统编码。 */
    private String systemCode;
    /** 计划开始时间。 */
    private LocalDateTime planStartTime;
    /** 最晚开始时间。 */
    private LocalDateTime latestStartTime;
    /** 计划结束时间。 */
    private LocalDateTime planEndTime;
    /** 实际开始时间。 */
    private LocalDateTime actualStartTime;
    /** 实际结束时间。 */
    private LocalDateTime actualEndTime;
    /** 当前耗时，单位分钟。 */
    private Integer currentCostMinutes;
    /** 预计结束时间。 */
    private LocalDateTime predictEndTime;
    /** 是否延迟。 */
    private Integer delayedFlag;
    /** 是否超时。 */
    private Integer timeoutFlag;
    /** 结果状态。 */
    private String resultStatus;
    /** 错误信息。 */
    private String errorMsg;
    /** 是否展示。 */
    private Integer isFlag;
    /** 当前状态，仅查询时计算。 */
    private String currentStatus;
    /** 创建时间。 */
    private LocalDateTime createdTime;
    /** 更新时间。 */
    private LocalDateTime updatedTime;
}
