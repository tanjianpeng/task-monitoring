package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MonitorTaskDependency {

    /** 主键ID。 */
    private String id;
    /** 当前任务编码。 */
    private String taskCode;
    /** 前置任务编码。 */
    private String preTaskCode;
    /** 方向。 */
    private String direction;
    /** 创建时间。 */
    private LocalDateTime createdTime;
    /** 更新时间。 */
    private LocalDateTime updatedTime;
}
