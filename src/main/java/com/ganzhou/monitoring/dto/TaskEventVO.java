package com.ganzhou.monitoring.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TaskEventVO {

    /** 请求流水号。 */
    private String requestId;
    /** 事件类型。 */
    private String eventType;
    /** 事件时间。 */
    private LocalDateTime eventTime;
    /** 结果状态。 */
    private String resultStatus;
    /** 错误码。 */
    private String errorCode;
    /** 错误信息。 */
    private String errorMessage;
}
