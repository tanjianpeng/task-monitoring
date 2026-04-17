package com.ganzhou.monitoring.service.impl;

import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.common.TaskSignUtils;
import com.ganzhou.monitoring.dto.TaskReportRequest;
import com.ganzhou.monitoring.service.TaskSignService;
import org.springframework.stereotype.Service;

/**
 * Description: 外部系统回调验签服务实现。 按“systemCode=xxx&taskCode=xxx&bizDate=xxx&status=xxx&requestTime=xxx”拼串后做 MD5。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Service
public class TaskSignServiceImpl implements TaskSignService {

    /**
     * 校验外部系统请求签名。
     *
     * @param request 外部系统上报请求报文
     */
    @Override
    public void verifySign(TaskReportRequest request) {
        String expected = TaskSignUtils.generateSign(request);
        if (!expected.equalsIgnoreCase(request.getSign())) {
            throw new BusinessException("sign验签失败");
        }
    }
}
