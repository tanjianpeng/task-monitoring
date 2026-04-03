package com.ganzhou.monitoring.dto;

import lombok.Data;

@Data
public class SummaryVO {

    /** 监控系统数。 */
    private Integer systemCount;
    /** 当日任务总数。 */
    private Integer taskCount;
    /** 待执行任务数。 */
    private Integer pendingCount;
    /** 执行中任务数。 */
    private Integer runningCount;
    /** 成功任务数。 */
    private Integer successCount;
    /** 失败任务数。 */
    private Integer failedCount;
    /** 延迟未启动任务数。 */
    private Integer delayedCount;
    /** 超时未结束任务数。 */
    private Integer timeoutCount;
}
