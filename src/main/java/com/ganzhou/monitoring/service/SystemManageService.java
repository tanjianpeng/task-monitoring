package com.ganzhou.monitoring.service;

import com.ganzhou.monitoring.dto.SystemSaveRequest;
import com.ganzhou.monitoring.entity.MonitorSystem;
import java.util.List;

/**
 * 系统主数据管理服务。
 */
public interface SystemManageService {

    List<MonitorSystem> listSystems();

    MonitorSystem getSystem(String systemCode);

    void createSystem(SystemSaveRequest request);

    void updateSystem(SystemSaveRequest request);

    void deleteSystem(String systemCode);
}
