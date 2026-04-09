package com.ganzhou.monitoring.service;

/**
 * Description: 任务运行配置查询服务。 真实项目中可在这里对接字典表、配置中心或其他主数据服务。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
public interface TaskRuntimeConfigService {

    /**
     * 预留根据 taskCode 查询验签密钥。
     *
     * @param taskCode 任务编码
     * @return 验签密钥，未查询到时返回 null
     */
    String querySignKeyByTaskCode(String taskCode);

    /**
     * 预留根据 taskCode 查询允许延迟分钟数。
     *
     * @param taskCode 任务编码
     * @return 允许延迟分钟数，未查询到时返回 null
     */
    Integer queryAllowDelayMinutesByTaskCode(String taskCode);

}
