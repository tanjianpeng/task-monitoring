package com.ganzhou.monitoring.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TaskCardVO {

    /** 任务编码。 */
    private String taskCode;
    /** 任务名称。 */
    private String taskName;
    /** 系统编码。 */
    private String systemCode;
    /** 系统名称。 */
    private String systemName;
    /** 负责人。 */
    private String ownerName;
    /** 监督人。 */
    private String supervisorName;
    /** 当前状态。 */
    private String status;
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
    /** 当前耗时，单位秒。 */
    private Integer currentCostSeconds;
    /** 历史平均耗时，单位秒。 */
    private Integer avgCostSeconds;
    /** 预计结束时间。 */
    private LocalDateTime predictEndTime;
}
