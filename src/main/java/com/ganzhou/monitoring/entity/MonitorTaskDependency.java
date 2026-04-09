package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 任务依赖实体类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@Table(name = "monitor_task_dependency")
@ApiModel(value = "任务依赖表")
public class MonitorTaskDependency {

    /** 主键ID。 */
    @Column(name = "id", length = 64)
    @ApiModelProperty(name = "id", value = "主键ID")
    private String id;

    /** 当前任务编码。 */
    @Column(name = "task_code", length = 64, nullable = false)
    @ApiModelProperty(name = "taskCode", value = "任务编码")
    private String taskCode;

    /** 前置任务编码。 */
    @Column(name = "pre_task_code", length = 64, nullable = false)
    @ApiModelProperty(name = "preTaskCode", value = "前置任务编码")
    private String preTaskCode;

    /** 方向。 */
    @Column(name = "direction", length = 64)
    @ApiModelProperty(name = "direction", value = "箭头方向")
    private String direction;

    /** 创建时间。 */
    @Column(name = "created_time", nullable = false)
    @ApiModelProperty(name = "createdTime", value = "创建时间")
    private LocalDateTime createdTime;

    /** 更新时间。 */
    @Column(name = "updated_time", nullable = false)
    @ApiModelProperty(name = "updatedTime", value = "更新时间")
    private LocalDateTime updatedTime;
}
