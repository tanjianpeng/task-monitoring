package com.ganzhou.monitoring.dto;

import lombok.Data;

/**
 * 任务依赖项。
 * 维护任务时，可同时提交多个前置依赖关系。
 */
@Data
public class TaskDependencyItem {

    /** 前置任务编码。 */
    private String preTaskCode;

    /** 节点方向。 */
    private String direction;
}
