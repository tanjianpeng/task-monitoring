package com.ganzhou.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Description: 任务上报请求对象。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
public class TaskReportRequest {

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
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate bizDate;

    /**
     * 运行批次。
     * 默认值为 0。
     */
    private Integer runNo = 0;

    /**
     * 本次上报状态。
     * 支持 start、end、restart、fail 四种取值。
     */
    private String status;

    /**
     * 任务开始时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 任务结束时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 耗时，单位秒。
     */
    private Integer costSeconds;

    /**
     * 请求时间。
     * 如果业务方没有显式传入，则后台使用服务端接收时间作为请求时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime;

    /**
     * 外部系统签名串。
     * 服务端会按预留的验签逻辑重新计算 MD5 并进行签名校验。
     */
    private String sign;

}
