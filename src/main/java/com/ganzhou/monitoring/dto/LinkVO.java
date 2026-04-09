package com.ganzhou.monitoring.dto;

import lombok.Data;

/**
 * Description: 依赖连线对象。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class LinkVO {

    /** 前置任务编码。 */
    private String preTaskCode;

    /** 当前任务编码。 */
    private String taskCode;

    /** 箭头方向。 */
    private String direction;
}
