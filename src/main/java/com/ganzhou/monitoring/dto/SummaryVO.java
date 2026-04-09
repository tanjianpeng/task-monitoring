package com.ganzhou.monitoring.dto;

import lombok.Data;

/**
 * Description: 汇总统计对象。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class SummaryVO {

    /** 监控系统数。 */
    private Integer systemCount;

    /** 当日任务总数。 */
    private Integer taskCount;

    /** 未开始任务数。 */
    private Integer notStartCount;

    /** 执行中任务数。 */
    private Integer runningCount;

    /** 成功任务数。 */
    private Integer successCount;

    /** 失败任务数。 */
    private Integer failedCount;
}
