package com.ganzhou.monitoring.dto;

import lombok.Data;

/**
 * Description: 任务依赖项。 维护任务时，可同时提交多个前置依赖关系。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class TaskDependencyItem {

    /** 前置任务编码。 */
    private String preTaskCode;

    /** 箭头指向方向，页面大屏可取 TOP、BOTTOM、LEFT、RIGHT。 */
    private String direction;
}
