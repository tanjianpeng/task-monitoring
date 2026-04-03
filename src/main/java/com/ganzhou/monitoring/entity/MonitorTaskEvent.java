package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MonitorTaskEvent {

    /** 主键ID。 */
    private String id;
    /** 请求流水号。 */
    private String requestId;
    /** 调用URL。 */
    private String requestUrl;
    /** 动作类型。 */
    private String actionType;
    /** 业务日期。 */
    private String bizDate;
    /** 运行批次。 */
    private Integer runTimes;
    /** 任务编码。 */
    private String taskCode;
    /** 上报系统编码。 */
    private String systemCode;
    /** 执行结果状态。 */
    private String resultStatus;
    /** 错误信息。 */
    private String errorMsg;
    /** 请求报文。 */
    private String requestJson;
    /** 创建时间。 */
    private LocalDateTime createdTime;
}
