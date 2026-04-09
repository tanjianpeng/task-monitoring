package com.ganzhou.monitoring.service;

import java.util.List;
import java.util.Map;

import com.ganzhou.monitoring.dto.SystemDashboardCardVO;
import com.ganzhou.monitoring.dto.TaskStatusDataVO;

/**
 * Description: 大屏服务接口。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public interface DashboardService {

    /**
     * 查询本地大屏顶部统计数据。
     */
    Map<String, String> getStatistics();

    /**
     * 查询本地大屏系统列表。
     */
    List<SystemDashboardCardVO> getSystems(String systemName);

    /**
     * 查询本地大屏任务状态列表。
     * 返回结果会按任务依赖关系做顺序整理，便于前端直接渲染链路。
     */
    TaskStatusDataVO getTaskStatusList();
}
