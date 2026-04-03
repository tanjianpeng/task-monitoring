package com.ganzhou.monitoring.service.impl;

import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.dto.SystemSaveRequest;
import com.ganzhou.monitoring.entity.MonitorSystem;
import com.ganzhou.monitoring.mapper.MonitorSystemManageMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskManageMapper;
import com.ganzhou.monitoring.service.SystemManageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统主数据管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class SystemManageServiceImpl implements SystemManageService {

    private final MonitorSystemManageMapper systemManageMapper;
    private final MonitorTaskManageMapper taskManageMapper;

    @Override
    public List<MonitorSystem> listSystems() {
        return systemManageMapper.selectAll();
    }

    @Override
    public MonitorSystem getSystem(String systemCode) {
        return systemManageMapper.selectByCode(systemCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSystem(SystemSaveRequest request) {
        if (systemManageMapper.selectByCode(request.getSystemCode()) != null) {
            throw new BusinessException("系统编码已存在: " + request.getSystemCode());
        }
        systemManageMapper.insert(toEntity(request));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystem(SystemSaveRequest request) {
        if (systemManageMapper.selectByCode(request.getSystemCode()) == null) {
            throw new BusinessException("系统不存在: " + request.getSystemCode());
        }
        systemManageMapper.updateByCode(toEntity(request));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSystem(String systemCode) {
        int taskCount = taskManageMapper.countBySystemCode(systemCode);
        if (taskCount > 0) {
            throw new BusinessException("该系统下仍存在任务，不能删除");
        }
        systemManageMapper.deleteByCode(systemCode);
    }

    private MonitorSystem toEntity(SystemSaveRequest request) {
        MonitorSystem entity = new MonitorSystem();
        entity.setSystemCode(request.getSystemCode());
        entity.setSystemName(request.getSystemName());
        entity.setOwnerName(request.getOwnerName());
        entity.setSupervisorName(request.getSupervisorName());
        entity.setStatus(request.getStatus() == null ? "1" : request.getStatus());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
