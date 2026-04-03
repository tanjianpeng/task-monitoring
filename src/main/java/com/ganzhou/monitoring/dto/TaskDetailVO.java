package com.ganzhou.monitoring.dto;

import java.util.List;
import lombok.Data;

@Data
public class TaskDetailVO {

    /** 任务实例详情。 */
    private TaskCardVO instance;
    /** 事件流水列表。 */
    private List<TaskEventVO> events;
}
