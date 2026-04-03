package com.ganzhou.monitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 任务主数据保存请求。
 * 包含任务本身信息以及依赖关系列表。
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

    /** 负责人。 */
    private String ownerName;

    /** 监督人。 */
    private String supervisorName;

    /** 前置依赖产物。 */
    private String preRequisiteProd;

    /** 本批处理任务产出物。 */
    private String theBatchProd;

    /** 计划开始时间。 */
    @NotNull
    private LocalDateTime planStartTime;

    /** 计划结束时间。 */
    @NotNull
    private LocalDateTime planEndTime;

    /** 默认耗时分钟数。 */
    private Integer defaultCostMinutes;

    /** 历史平均耗时分钟数。 */
    private Integer avgCostMinutes;

    /** 延迟分钟数。 */
    private Integer delayMinutes;

    /** 频率。 */
    private String frequency;

    /** 是否展示。 */
    private String displayFlag;

    /** 横坐标。 */
    private Integer posX;

    /** 纵坐标。 */
    private Integer posY;

    /** 状态。 */
    private String status;

    /** 备注。 */
    private String remark;

    /** 依赖关系列表。 */
    private List<TaskDependencyItem> dependencies;
}
