package com.ganzhou.monitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

@Data
public class TaskReportRequest {

    /**
     * 请求流水号。
     * 用于记录外部系统本次调用的请求标识，便于日志追踪。
     */
    @NotBlank
    private String requestId;

    /**
     * 上报系统编码。
     */
    @NotBlank
    private String systemCode;

    /**
     * 任务编码。
     */
    @NotBlank
    private String taskCode;

    /**
     * 业务日期。
     * 一般指当前跑批归属的业务日，而不是接口调用日期。
     */
    @NotNull
    private LocalDate bizDate;

    /**
     * 运行批次。
     * 默认值为 0。
     */
    private Integer runNo = 0;

    /**
     * 任务开始时间。
     */
    private LocalDateTime startTime;

    /**
     * 任务结束时间。
     */
    private LocalDateTime endTime;

    /**
     * 上报结果状态。
     */
    private String resultStatus;

    /**
     * 耗时，单位秒。
     */
    private Integer costSeconds;

    /**
     * 错误描述。
     */
    private String errorMessage;

    /**
     * 操作人。
     */
    private String operator;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 扩展报文。
     * 用于存储进度、节点信息、附加业务标识等非标准字段。
     */
    private Map<String, Object> ext;
}
