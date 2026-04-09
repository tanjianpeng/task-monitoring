package com.ganzhou.monitoring.dto;

import java.util.List;

import com.ganzhou.monitoring.entity.MonitorTaskDef;
import lombok.Data;

/**
 * Description: 任务管理详情返回对象。 页面编辑任务时可一次性拿到任务主数据和依赖关系。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class TaskManageDetailVO {

    /** 任务主数据。 */
    private MonitorTaskDef task;

    /** 任务依赖关系。 */
    private List<TaskDependencyItem> dependencies;
}
