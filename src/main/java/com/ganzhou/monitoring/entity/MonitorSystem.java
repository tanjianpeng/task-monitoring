package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 系统实体类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@Table(name = "monitor_system")
@ApiModel(value = "监控系统表")
public class MonitorSystem {

    /** 系统编码，主键。 */
    @Id
    @Column(name = "system_code", unique = true, length = 64, nullable = false)
    @ApiModelProperty(name = "systemCode", value = "系统编码")
    private String systemCode;

    /** 系统名称。 */
    @Column(name = "system_name", length = 128, nullable = false)
    @ApiModelProperty(name = "systemName", value = "系统名称")
    private String systemName;

    /** 系统负责人。 */
    @Column(name = "owner_name", length = 64)
    @ApiModelProperty(name = "ownerName", value = "系统负责人")
    private String ownerName;

    /** 监督人。 */
    @Column(name = "supervisor_name", length = 64)
    @ApiModelProperty(name = "supervisorName", value = "监督人")
    private String supervisorName;

    /** 是否启用状态。 */
    @Column(name = "is_flag", length = 1, nullable = false)
    @ApiModelProperty(name = "isFlag", value = "是否启用状态，0启用，1停用")
    private String isFlag;

    /** 备注。 */
    @Column(name = "remark", length = 512)
    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    /** 创建时间。 */
    @Column(name = "created_time", nullable = false)
    @ApiModelProperty(name = "createdTime", value = "创建时间")
    private LocalDateTime createdTime;

    /** 更新时间。 */
    @Column(name = "updated_time", nullable = false)
    @ApiModelProperty(name = "updatedTime", value = "更新时间")
    private LocalDateTime updatedTime;
}
