package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 任务实例实体类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@Table(name = "monitor_task_instance")
@ApiModel(value = "任务实例表")
public class MonitorTaskInstance {

    /** 主键ID。 */
    @Id
    @Column(name = "id", unique = true, length = 64)
    @ApiModelProperty(name = "id", value = "主键ID")
    private String id;

    /** 业务日期。 */
    @Column(name = "biz_date", length = 64, nullable = false)
    @ApiModelProperty(name = "bizDate", value = "业务日期")
    private String bizDate;

    /** 重跑次数。 */
    @Column(name = "run_times", nullable = false)
    @ApiModelProperty(name = "runTimes", value = "重跑次数")
    private Integer runTimes;

    /** 任务编码。 */
    @Column(name = "task_code", length = 64, nullable = false)
    @ApiModelProperty(name = "taskCode", value = "任务编码")
    private String taskCode;

    /** 系统编码。 */
    @Column(name = "system_code", length = 64, nullable = false)
    @ApiModelProperty(name = "systemCode", value = "系统编码")
    private String systemCode;

    /** 计划开始时间。 */
    @Column(name = "plan_start_time", nullable = false)
    @ApiModelProperty(name = "planStartTime", value = "计划开始时间")
    private LocalDateTime planStartTime;

    /** 最晚开始时间。 */
    @Column(name = "latest_start_time")
    @ApiModelProperty(name = "latestStartTime", value = "最晚开始时间")
    private LocalDateTime latestStartTime;

    /** 计划结束时间。 */
    @Column(name = "plan_end_time")
    @ApiModelProperty(name = "planEndTime", value = "计划结束时间")
    private LocalDateTime planEndTime;

    /** 实际开始时间。 */
    @Column(name = "actual_start_time")
    @ApiModelProperty(name = "actualStartTime", value = "实际开始时间")
    private LocalDateTime actualStartTime;

    /** 实际结束时间。 */
    @Column(name = "actual_end_time")
    @ApiModelProperty(name = "actualEndTime", value = "实际结束时间")
    private LocalDateTime actualEndTime;

    /** 当前耗时，单位分钟。 */
    @Column(name = "current_cost_minutes")
    @ApiModelProperty(name = "currentCostMinutes", value = "当前耗时(分钟)")
    private Integer currentCostMinutes;

    /** 历史平均耗时，单位分钟。 */
    @Column(name = "avg_cost_minutes")
    @ApiModelProperty(name = "avgCostMinutes", value = "历史平均耗时(分钟)")
    private Integer avgCostMinutes;

    /** 频率。 */
    @Column(name = "frequency", length = 1)
    @ApiModelProperty(name = "frequency", value = "频率，平日D，月底M")
    private String frequency;

    /** 预计结束时间。 */
    @Column(name = "predict_end_time")
    @ApiModelProperty(name = "predictEndTime", value = "预计结束时间")
    private LocalDateTime predictEndTime;

    /** 是否延迟。 */
    @Column(name = "delayed_flag", nullable = false)
    @ApiModelProperty(name = "delayedFlag", value = "是否延迟")
    private Integer delayedFlag;

    /** 是否超时。 */
    @Column(name = "timeout_flag", nullable = false)
    @ApiModelProperty(name = "timeoutFlag", value = "是否超时")
    private Integer timeoutFlag;

    /** 结果状态。 */
    @Column(name = "result_status", length = 32)
    @ApiModelProperty(name = "resultStatus", value = "执行结果状态")
    private String resultStatus;

    /** 是否展示。 */
    @Column(name = "is_flag", length = 1, nullable = false)
    @ApiModelProperty(name = "isFlag", value = "是否展示，1否，0是")
    private String isFlag;

    /** 当前状态，仅查询时计算。 */
    @Transient
    @ApiModelProperty(name = "currentStatus", value = "当前状态")
    private String currentStatus;

    /** 创建时间。 */
    @Column(name = "created_time")
    @ApiModelProperty(name = "createdTime", value = "创建时间")
    private LocalDateTime createdTime;

    /** 更新时间。 */
    @Column(name = "updated_time")
    @ApiModelProperty(name = "updatedTime", value = "更新时间")
    private LocalDateTime updatedTime;
}
