package com.ganzhou.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchMonitoringApplication {

    /**
     * Spring Boot 启动入口。
     * 该应用用于承载跑批监控平台的统一上报接口和大屏查询接口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(BatchMonitoringApplication.class, args);
    }
}
