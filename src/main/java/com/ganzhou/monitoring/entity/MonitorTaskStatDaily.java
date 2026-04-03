package com.ganzhou.monitoring.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MonitorTaskStatDaily {

    /** 主键ID。 */
    private Long id;
    /** 业务日期。 */
    private LocalDate bizDate;
    /** 任务编码。 */
    private String taskCode;
    /** 系统编码。 */
    private String systemCode;
    /** 运行批次。 */
    private Integer runNo;
    /** 是否成功。 */
    private String successFlag;
    /** 是否延迟。 */
    private String delayedFlag;
    /** 是否超时。 */
    private String timeoutFlag;
    /** 是否月末。 */
    private String monthEndFlag;
    /** 实际耗时。 */
    private Integer costSeconds;
    /** 创建时间。 */
    private LocalDateTime createdAt;
    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
