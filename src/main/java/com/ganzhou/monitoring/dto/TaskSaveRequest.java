package com.ganzhou.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Description: 任务主数据保存请求。 包含任务本身信息以及依赖关系列表。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class TaskSaveRequest {

    /** 任务编码。 */
    @NotBlank
    private String taskCode;

    /** 任务名称。 */
    @NotBlank
    private String taskName;

    /** 所属系统编码。 */
    @NotBlank
    private String systemCode;

    /** 前置依赖产物。 */
    private String preRequisiteProd;

    /** 本批处理任务产出物。 */
    private String theBatchProd;

    /** 计划开始时间，格式 HH:mm:ss。 */
    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime planStartTime;

    /** 计划结束时间，格式 HH:mm:ss。 */
    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime planEndTime;

    /** 默认耗时分钟数。 */
    private Integer defaultCostMinutes;

    /** 跑批周期。 */
    private String batchProcessing;

    /** 是否关键路径。 */
    private String isPath;

    /** 横坐标。 */
    private Integer posX;

    /** 纵坐标。 */
    private Integer posY;

    /** 是否大屏展示，0是，1否。 */
    private String isFlag;

    /** 备注。 */
    private String remark;

    /** 依赖关系列表。 */
    private List<TaskDependencyItem> dependencies;
}
