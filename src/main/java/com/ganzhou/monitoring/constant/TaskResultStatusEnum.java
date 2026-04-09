package com.ganzhou.monitoring.constant;

/**
 * Description: 任务实例执行结果状态枚举。 用于统一维护跑批监控中的所有运行态状态码，避免业务代码中散落硬编码字符串。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public enum TaskResultStatusEnum {

    /**
     * 未开始。
     * 任务实例已生成，但外部系统还没有回传开始时间。
     */
    NOTSTART("NOTSTART", "待执行"),

    /**
     * 延迟。
     * 任务未按计划时间完成推进时统一记为延迟。
     * 包括三种场景：一是开始时间超过最晚开始时间，
     * 二是已经开始但已超过预计结束时间仍未结束，
     * 三是任务已经结束，但实际结束时间已超过预计结束时间。
     */
    DELAYED("DELAYED", "延迟"),

    /**
     * 运行中。
     * 已收到 start 或 restart 回调，但尚未收到 stop 或 fail 回调。
     */
    RUNNING("RUNNING", "执行中"),

    /**
     * 成功。
     * 任务已经正常结束，且最终结果为成功。
     */
    SUCCESS("SUCCESS", "成功"),

    /**
     * 失败。
     * 任务已经结束，但最终结果为失败。
     */
    FAILED("FAILED", "失败");

    /**
     * 状态编码。
     */
    private final String code;

    /**
     * 状态中文说明。
     */
    private final String description;

    TaskResultStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
