package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MonitorSystem {

    /** 系统编码，主键。 */
    private String systemCode;
    /** 系统名称。 */
    private String systemName;
    /** 系统负责人。 */
    private String ownerName;
    /** 监督人。 */
    private String supervisorName;
    /** 系统状态。 */
    private String status;
    /** 备注。 */
    private String remark;
    /** 创建时间。 */
    private LocalDateTime createdTime;
    /** 更新时间。 */
    private LocalDateTime updatedTime;
}
