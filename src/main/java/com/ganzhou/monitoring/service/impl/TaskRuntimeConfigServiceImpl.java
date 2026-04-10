package com.ganzhou.monitoring.service.impl;

import com.ganzhou.monitoring.service.TaskRuntimeConfigService;
import org.springframework.stereotype.Service;

/**
 * Description: 任务运行配置查询服务实现。 当前仅预留扩展口子，后续可替换为真实字典表或远程配置查询。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Service
public class TaskRuntimeConfigServiceImpl implements TaskRuntimeConfigService {

    /**
     * 预留根据 taskCode 查询验签密钥的方法。
     * 真实项目中请在这里接入你们现有的字段/字典/配置查询逻辑。
     *
     * @param taskCode 任务编码
     */
    @Override
    public String querySignKeyByTaskCode(String taskCode) {
        return null;
    }

    /**
     * 预留根据 taskCode 查询允许延迟分钟数的方法。
     * 真实项目中请在这里接入你们现有的字段/字典/配置查询逻辑。
     *
     * @param taskCode 任务编码
     */
    @Override
    public Integer queryAllowDelayMinutesByTaskCode(String taskCode) {
        return null;
    }
}
