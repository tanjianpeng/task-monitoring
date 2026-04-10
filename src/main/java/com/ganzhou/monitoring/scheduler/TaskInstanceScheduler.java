package com.ganzhou.monitoring.scheduler;

import com.ganzhou.monitoring.mapper.MonitorTaskInstanceMapper;
import com.ganzhou.monitoring.mapper.SchedulerLockMapper;
import com.ganzhou.monitoring.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Description: 任务实例定时初始化调度器。 每天 0 点按任务定义预生成当天实例，保证大屏在任务开始前就能拿到完整实例数据。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskInstanceScheduler {

    /**
     * 业务日期格式。
     */
    private static final DateTimeFormatter BIZ_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * 跑批监控核心服务。
     */
    private final MonitoringService monitoringService;

    /**
     * 调度锁 Mapper。
     */
    private final SchedulerLockMapper schedulerLockMapper;

    /**
     * 任务实例查询 Mapper。
     * 用于读取实例表最新业务日期，和本地大屏查询保持一致。
     */
    private final MonitorTaskInstanceMapper taskInstanceMapper;

    /**
     * 是否启用数据库锁。
     */
    @Value("${monitor.scheduler.db-lock-enabled:true}")
    private boolean dbLockEnabled;

    /**
     * 数据库锁等待秒数。
     */
    @Value("${monitor.scheduler.db-lock-timeout-seconds:0}")
    private Integer dbLockTimeoutSeconds;

    /**
     * 每天凌晨 0 点初始化当天任务实例。
     * 获取到数据库命名锁的节点才允许继续执行，执行结束后统一在 finally 中释放锁。
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void initializeTodayTaskInstances() {
        LocalDate bizDate = resolveInitializeBizDate();
        String lockName = "task-monitoring:init-task-instance:" + bizDate.format(BIZ_DATE_FORMATTER);
        boolean lockAcquired = false;
        try {
            lockAcquired = !dbLockEnabled || tryAcquireLock(lockName);
            if (!lockAcquired) {
                log.info("未获取到定时任务锁，跳过本次实例初始化，bizDate={}, lockName={}", bizDate, lockName);
                return;
            }
            int affected = monitoringService.initializeTaskInstances(bizDate);
            log.info("每日任务实例初始化完成，bizDate={}, affected={}, lockName={}", bizDate, affected, lockName);
        } finally {
            if (lockAcquired && dbLockEnabled) {
                releaseLock(lockName);
            }
        }
    }

    /**
     * 解析本次凌晨初始化使用的业务日期。
     * 口径与本地大屏查询一致：优先取实例表最新一条业务日期，实例表为空时回退到昨天日期。
     */
    private LocalDate resolveInitializeBizDate() {
        String latestBizDate = taskInstanceMapper.selectLatestBizDate();
        if (latestBizDate == null || latestBizDate.isBlank()) {
            return LocalDate.now().minusDays(1);
        }
        return LocalDate.parse(latestBizDate, BIZ_DATE_FORMATTER);
    }

    /**
     * 尝试获取数据库命名锁。
     *
     * @param lockName 数据库命名锁名称
     */
    private boolean tryAcquireLock(String lockName) {
        Integer locked = schedulerLockMapper.acquireLock(lockName, dbLockTimeoutSeconds);
        return Integer.valueOf(1).equals(locked);
    }

    /**
     * 释放数据库命名锁。
     *
     * @param lockName 数据库命名锁名称
     */
    private void releaseLock(String lockName) {
        try {
            Integer released = schedulerLockMapper.releaseLock(lockName);
            log.info("定时任务锁释放完成，lockName={}, released={}", lockName, released);
        } catch (Exception ex) {
            log.warn("定时任务锁释放失败，lockName={}", lockName, ex);
        }
    }
}
