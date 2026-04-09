package com.ganzhou.monitoring.dto;

import lombok.Data;

/**
 * Description: 本地大屏系统信息卡片返回对象。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class SystemDashboardCardVO {

    /** 系统编码。 */
    private String systemCode;

    /** 系统名称。 */
    private String systemName;

    /** 系统负责人。 */
    private String ownerName;

    /** 系统监督人。 */
    private String supervisorName;

    /** 待执行数量。 */
    private Integer notStartCount;

    /** 执行中数量。 */
    private Integer runningCount;

    /** 成功数量。 */
    private Integer successCount;

    /** 失败数量。 */
    private Integer failedCount;

    /** 状态统计总数。 */
    private Integer totalCount;

}
