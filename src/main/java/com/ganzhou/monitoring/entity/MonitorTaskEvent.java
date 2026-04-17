package com.ganzhou.monitoring.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 任务调用日志实体类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@Table(name = "monitor_task_log")
@ApiModel(value = "任务调用日志表")
public class MonitorTaskEvent {

    /** 主键ID。 */
    @Id
    @Column(name = "id", unique = true, length = 64, nullable = false)
    @ApiModelProperty(name = "id", value = "主键ID")
    private String id;

    /** 调用URL。 */
    @Column(name = "request_url", length = 200, nullable = false)
    @ApiModelProperty(name = "requestUrl", value = "调用URL")
    private String requestUrl;

    /** 请求动作状态，对应日志表 request_status 字段。 */
    @Column(name = "request_status", length = 32, nullable = false)
    @ApiModelProperty(name = "requestStatus", value = "请求动作状态")
    private String requestStatus;

    /** 业务日期。 */
    @Column(name = "biz_date", length = 64, nullable = false)
    @ApiModelProperty(name = "bizDate", value = "业务日期")
    private String bizDate;

    /** 运行批次。 */
    @Column(name = "run_times", nullable = false)
    @ApiModelProperty(name = "runTimes", value = "运行批次")
    private Integer runTimes;

    /** 任务编码。 */
    @Column(name = "task_code", length = 64, nullable = false)
    @ApiModelProperty(name = "taskCode", value = "任务编码")
    private String taskCode;

    /** 上报系统编码。 */
    @Column(name = "system_code", length = 64, nullable = false)
    @ApiModelProperty(name = "systemCode", value = "所属系统编码")
    private String systemCode;

    /** 执行结果状态。 */
    @Column(name = "result_status", length = 32)
    @ApiModelProperty(name = "resultStatus", value = "执行结果状态")
    private String resultStatus;

    /** 请求报文。 */
    @Column(name = "request_json", columnDefinition = "TEXT")
    @ApiModelProperty(name = "requestJson", value = "请求报文")
    private String requestJson;

    /** 创建时间。 */
    @Column(name = "created_time", nullable = false)
    @ApiModelProperty(name = "createdTime", value = "创建时间")
    private LocalDateTime createdTime;
}
