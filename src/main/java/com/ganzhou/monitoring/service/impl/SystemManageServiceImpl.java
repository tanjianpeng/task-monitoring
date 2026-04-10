package com.ganzhou.monitoring.service.impl;

import java.util.List;

import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.dto.SystemSaveRequest;
import com.ganzhou.monitoring.entity.MonitorSystem;
import com.ganzhou.monitoring.mapper.MonitorSystemManageMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskManageMapper;
import com.ganzhou.monitoring.service.SystemManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 系统主数据管理服务实现。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemManageServiceImpl implements SystemManageService {

    private final MonitorSystemManageMapper systemManageMapper;
    private final MonitorTaskManageMapper taskManageMapper;

    /**
     * 查询系统列表。
     */
    @Override
    public List<MonitorSystem> listSystems() {
        return systemManageMapper.selectAll();
    }

    /**
     * 查询系统详情。
     *
     * @param systemCode 系统编码
     */
    @Override
    public MonitorSystem getSystem(String systemCode) {
        return systemManageMapper.selectByCode(systemCode);
    }

    /**
     * 新增系统主数据。
     *
     * @param request 系统新增请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSystem(SystemSaveRequest request) {
        try {
            if (systemManageMapper.selectByCode(request.getSystemCode()) != null) {
                throw new BusinessException("系统编码已存在: " + request.getSystemCode());
            }
            systemManageMapper.insert(toEntity(request));
        } catch (RuntimeException ex) {
            log.error("新增系统主数据异常，systemCode={}", request.getSystemCode(), ex);
            throw ex;
        }
    }

    /**
     * 修改系统主数据。
     *
     * @param request 系统修改请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystem(SystemSaveRequest request) {
        try {
            if (systemManageMapper.selectByCode(request.getSystemCode()) == null) {
                throw new BusinessException("系统不存在: " + request.getSystemCode());
            }
            systemManageMapper.updateByCode(toEntity(request));
        } catch (RuntimeException ex) {
            log.error("修改系统主数据异常，systemCode={}", request.getSystemCode(), ex);
            throw ex;
        }
    }

    /**
     * 删除系统主数据。
     *
     * @param systemCode 系统编码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSystem(String systemCode) {
        try {
            int taskCount = taskManageMapper.countBySystemCode(systemCode);
            if (taskCount > 0) {
                throw new BusinessException("该系统下仍存在任务，不能删除");
            }
            systemManageMapper.deleteByCode(systemCode);
        } catch (RuntimeException ex) {
            log.error("删除系统主数据异常，systemCode={}", systemCode, ex);
            throw ex;
        }
    }

    /**
     * 将系统保存请求转换为数据库实体。
     *
     * @param request 系统保存请求参数
     */
    private MonitorSystem toEntity(SystemSaveRequest request) {
        MonitorSystem entity = new MonitorSystem();
        entity.setSystemCode(request.getSystemCode());
        entity.setSystemName(request.getSystemName());
        entity.setOwnerName(request.getOwnerName());
        entity.setSupervisorName(request.getSupervisorName());
        entity.setIsFlag(request.getIsFlag() == null ? "0" : request.getIsFlag());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
