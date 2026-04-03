package com.ganzhou.monitoring.dto;

import com.ganzhou.monitoring.entity.MonitorTaskDef;
import java.util.List;
import lombok.Data;

/**
 * 任务管理详情返回对象。
 * 页面编辑任务时可一次性拿到任务主数据和依赖关系。
 */
@Data
public class TaskManageDetailVO {

    /** 任务主数据。 */
    private MonitorTaskDef task;

    /** 任务依赖关系。 */
    private List<TaskDependencyItem> dependencies;
}
