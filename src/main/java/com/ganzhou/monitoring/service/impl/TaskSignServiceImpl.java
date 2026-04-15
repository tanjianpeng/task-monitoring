package com.ganzhou.monitoring.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.ganzhou.monitoring.common.BusinessException;
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
     * 业务日期格式。
     */
    private static final DateTimeFormatter BIZ_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * 请求时间格式。
     */
    private static final DateTimeFormatter REQUEST_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 校验外部系统请求签名。
     *
     * @param request 外部系统上报请求报文
     */
    @Override
    public void verifySign(TaskReportRequest request) {
        String plainText = buildPlainText(request);
        String expected = md5Hex(plainText);
        if (!expected.equalsIgnoreCase(request.getSign())) {
            throw new BusinessException("sign验签失败");
        }
    }

    /**
     * 按外部系统约定拼接参与验签的原文字符串。
     * 固定顺序为 systemCode、taskCode、bizDate、status、requestTime。
     *
     * @param request 外部系统上报请求报文
     */
    private String buildPlainText(TaskReportRequest request) {
        return new StringBuilder()
                .append("systemCode=").append(defaultString(request.getSystemCode()))
                .append("&taskCode=").append(defaultString(request.getTaskCode()))
                .append("&bizDate=").append(request.getBizDate() == null ? "" : request.getBizDate().format(BIZ_DATE_FORMATTER))
                .append("&status=").append(normalizeStatus(request.getStatus()))
                .append("&requestTime=").append(request.getRequestTime() == null ? ""
                        : request.getRequestTime().format(REQUEST_TIME_FORMATTER))
                .toString();
    }

    /**
     * 将动作统一转成小写，避免外部系统大小写差异影响验签。
     *
     * @param status 外部系统上报的动作状态
     */
    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 空字符串保护。
     *
     * @param value 原始字符串
     */
    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    /**
     * 计算 MD5 十六进制字符串。
     *
     * @param value 待加密字符串
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
