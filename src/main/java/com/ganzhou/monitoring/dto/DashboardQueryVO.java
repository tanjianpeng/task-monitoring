package com.ganzhou.monitoring.dto;

import java.util.List;

import com.ganzhou.monitoring.entity.MonitorSystem;
import lombok.Data;

/**
 * Description: 本地大屏聚合查询返回对象。 按前端页面布局拆成统计数据、系统列表、任务列表三个 list。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class DashboardQueryVO {

    /** 顶部统计数据。 */
    private List<StatisticItemVO> statistics;

    /** 系统列表。 */
    private List<MonitorSystem> systems;

    /** 任务及依赖关系列表。 */
    private List<TaskDashboardItemVO> tasks;
}
