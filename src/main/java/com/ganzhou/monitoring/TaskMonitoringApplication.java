package com.ganzhou.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Description: 任务监控应用启动类。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class TaskMonitoringApplication {

    /**
     * Spring Boot 启动入口。
     * 该应用用于承载跑批监控平台的统一上报接口和大屏查询接口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskMonitoringApplication.class, args);
    }
}
