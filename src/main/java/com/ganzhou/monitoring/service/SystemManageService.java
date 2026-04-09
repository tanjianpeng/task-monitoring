package com.ganzhou.monitoring.service;

import java.util.List;

import com.ganzhou.monitoring.dto.SystemSaveRequest;
import com.ganzhou.monitoring.entity.MonitorSystem;

/**
 * Description: 系统主数据管理服务。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public interface SystemManageService {

    List<MonitorSystem> listSystems();

    MonitorSystem getSystem(String systemCode);

    void createSystem(SystemSaveRequest request);

    void updateSystem(SystemSaveRequest request);

    void deleteSystem(String systemCode);
}
