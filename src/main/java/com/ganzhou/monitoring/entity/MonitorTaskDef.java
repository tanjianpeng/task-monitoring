package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 任务定义实体类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@Table(name = "monitor_task")
@ApiModel(value = "监控任务表")
public class MonitorTaskDef {

    /** 任务编码，主键。 */
    @Id
    @Column(name = "task_code", unique = true, length = 64, nullable = false)
    @ApiModelProperty(name = "taskCode", value = "任务编码")
    private String taskCode;

    /** 任务名称。 */
    @Column(name = "task_name", length = 128, nullable = false)
    @ApiModelProperty(name = "taskName", value = "任务名称")
    private String taskName;

    /** 所属系统编码。 */
    @Column(name = "system_code", length = 64, nullable = false)
    @ApiModelProperty(name = "systemCode", value = "所属系统编码")
    private String systemCode;

    /** 前置依赖产物。 */
    @Column(name = "pre_requisite_prod", length = 512)
    @ApiModelProperty(name = "preRequisiteProd", value = "前置依赖产物")
    private String preRequisiteProd;

    /** 本批处理任务产出物。 */
    @Column(name = "the_batch_prod", length = 512)
    @ApiModelProperty(name = "theBatchProd", value = "本批处理任务产出物")
    private String theBatchProd;

    /** 计划开始时间，格式 HH:mm:ss。 */
    @Column(name = "plan_start_time", nullable = false)
    @ApiModelProperty(name = "planStartTime", value = "计划开始时间，格式 HH:mm:ss")
    private LocalTime planStartTime;

    /** 计划结束时间，格式 HH:mm:ss。 */
    @Column(name = "plan_end_time", nullable = false)
    @ApiModelProperty(name = "planEndTime", value = "计划结束时间，格式 HH:mm:ss")
    private LocalTime planEndTime;

    /** 预计耗时，单位分钟。 */
    @Column(name = "default_cost_minutes")
    @ApiModelProperty(name = "defaultCostMinutes", value = "预计耗时(分钟)")
    private Integer defaultCostMinutes;

    /** 跑批周期。 */
    @Column(name = "batch_processing", length = 10)
    @ApiModelProperty(name = "batchProcessing", value = "跑批周期，D:每日 M:月末 NM:非月末")
    private String batchProcessing;

    /** 是否关键路径。 */
    @Column(name = "is_path", length = 1, nullable = false)
    @ApiModelProperty(name = "isPath", value = "是否关键路径，0是，1否")
    private String isPath;

    /** 横坐标。 */
    @Column(name = "pos_x", nullable = false)
    @ApiModelProperty(name = "posX", value = "横坐标X")
    private Integer posX;

    /** 纵坐标。 */
    @Column(name = "pos_y", nullable = false)
    @ApiModelProperty(name = "posY", value = "纵坐标Y")
    private Integer posY;

    /** 是否大屏展示，0是，1否。 */
    @Column(name = "is_flag", length = 1, nullable = false)
    @ApiModelProperty(name = "isFlag", value = "是否大屏展示，0是，1否")
    private String isFlag;

    /** 备注。 */
    @Column(name = "remark", length = 200)
    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    /** 创建时间。 */
    @Column(name = "created_time", nullable = false)
    @ApiModelProperty(name = "createdTime", value = "创建时间")
    private LocalDateTime createdTime;

    /** 更新时间。 */
    @Column(name = "updated_time", nullable = false)
    @ApiModelProperty(name = "updatedTime", value = "更新时间")
    private LocalDateTime updatedTime;
}
