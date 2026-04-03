package com.ganzhou.monitoring.dto;

import lombok.Data;

@Data
public class LinkVO {

    /** 起始任务编码。 */
    private String fromTaskCode;
    /** 目标任务编码。 */
    private String toTaskCode;
    /** 依赖类型。 */
    private String dependencyType;
    /** 是否强依赖。 */
    private String strongDependencyFlag;
    /** 输出物说明。 */
    private String outputDesc;
}
