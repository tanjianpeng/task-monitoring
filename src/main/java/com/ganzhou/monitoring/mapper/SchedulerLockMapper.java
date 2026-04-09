package com.ganzhou.monitoring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 调度锁 Mapper。 使用数据库命名锁保证多机部署时同一个定时任务只会执行一次。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface SchedulerLockMapper {

    /**
     * 尝试获取命名锁。
     *
     * @param lockName 锁名称，通常按“任务名+业务日期”拼接，用于标识本次调度
     * @param timeoutSeconds 获取锁等待秒数，0 表示立即返回不等待
     * @return 1 表示成功，0 表示未获取到，null 表示异常
     */
    Integer acquireLock(@Param("lockName") String lockName,
                        @Param("timeoutSeconds") Integer timeoutSeconds);

    /**
     * 释放命名锁。
     *
     * @param lockName 锁名称，必须和获取锁时使用的名称保持一致
     * @return 1 表示成功释放
     */
    Integer releaseLock(@Param("lockName") String lockName);
}
