package com.ganzhou.monitoring.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.ganzhou.monitoring.common.BusinessException;
import com.ganzhou.monitoring.dto.TaskReportRequest;
import com.ganzhou.monitoring.service.TaskRuntimeConfigService;
import com.ganzhou.monitoring.service.TaskSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Description: 外部系统回调验签服务实现。 按“signKey + systemCode + taskCode + bizDate + status + requestTime”拼串后做 MD5。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class TaskSignServiceImpl implements TaskSignService {

    /**
     * 任务运行配置查询服务。
     * 用于预留根据 taskCode 查询签名密钥的扩展能力。
     */
    private final TaskRuntimeConfigService taskRuntimeConfigService;

    /**
     * 业务日期格式。
     */
    private static final DateTimeFormatter BIZ_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 请求时间格式。
     */
    private static final DateTimeFormatter REQUEST_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void verifySign(TaskReportRequest request) {
        String signKey = taskRuntimeConfigService.querySignKeyByTaskCode(request.getTaskCode());
        if (signKey == null || signKey.isBlank()) {
            throw new BusinessException("任务未配置验签密钥，请实现根据taskCode查询签名字段的方法");
        }
        String plainText = signKey
                + request.getSystemCode()
                + request.getTaskCode()
                + request.getBizDate().format(BIZ_DATE_FORMATTER)
                + normalizeStatus(request.getStatus())
                + request.getRequestTime().format(REQUEST_TIME_FORMATTER);
        String expected = md5Hex(plainText);
        if (!expected.equalsIgnoreCase(request.getSign())) {
            throw new BusinessException("sign验签失败");
        }
    }

    /**
     * 将动作统一转成小写，避免外部系统大小写差异影响验签。
     */
    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 计算 MD5 十六进制字符串。
     */
    private String md5Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte current : bytes) {
                builder.append(String.format("%02x", current));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new BusinessException("系统不支持MD5验签");
        }
    }
}
