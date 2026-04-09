package com.ganzhou.monitoring.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Description: 任务调用事件对象。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class TaskEventVO {

    /** 请求流水号。 */
    private String requestId;

    /** 请求动作状态。 */
    private String requestStatus;

    /** 调用时间。 */
    private LocalDateTime eventTime;

    /** 系统计算后的结果状态。 */
    private String resultStatus;
}
