package com.ganzhou.monitoring.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Description: 系统信息保存请求。 用于页面对监控系统主数据进行新增或修改。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class SystemSaveRequest {

    /** 系统编码。 */
    @NotBlank
    private String systemCode;

    /** 系统名称。 */
    @NotBlank
    private String systemName;

    /** 系统负责人。 */
    private String ownerName;

    /** 监督人。 */
    private String supervisorName;

    /** 是否启用状态。 */
    private String isFlag;

    /** 备注。 */
    private String remark;
}
