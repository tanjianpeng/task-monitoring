package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MonitorTaskDef {

    /** 任务编码，主键。 */
    private String taskCode;
    /** 任务名称。 */
    private String taskName;
    /** 所属系统编码。 */
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
    private LocalDateTime planStartTime;
    /** 计划结束时间。 */
    private LocalDateTime planEndTime;
    /** 预计耗时，单位分钟。 */
    private Integer defaultCostMinutes;
    /** 历史平均耗时，单位分钟。 */
    private Integer avgCostMinutes;
    /** 延迟分钟数。 */
    private Integer delayMinutes;
    /** 频率。 */
    private String frequency;
    /** 是否在大屏展示。 */
    private String displayFlag;
    /** 横坐标。 */
    private Integer posX;
    /** 纵坐标。 */
    private Integer posY;
    /** 状态。 */
    private String status;
    /** 备注。 */
    private String remark;
    /** 创建时间。 */
    private LocalDateTime createdTime;
    /** 更新时间。 */
    private LocalDateTime updatedTime;
}
