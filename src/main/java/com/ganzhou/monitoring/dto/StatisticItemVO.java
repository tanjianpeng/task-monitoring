package com.ganzhou.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 大屏顶部统计项。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticItemVO {

    /** 统计项编码。 */
    private String itemCode;

    /** 统计项名称。 */
    private String itemName;

    /** 统计值。 */
    private Integer itemValue;
}
