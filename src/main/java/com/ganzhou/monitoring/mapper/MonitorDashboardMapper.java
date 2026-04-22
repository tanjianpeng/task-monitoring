package com.ganzhou.monitoring.mapper;

import java.util.List;

import com.ganzhou.monitoring.dto.LinkVO;
import com.ganzhou.monitoring.dto.SummaryVO;
import com.ganzhou.monitoring.dto.SystemDashboardCardVO;
import com.ganzhou.monitoring.dto.TaskCardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 大屏查询 Mapper。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface MonitorDashboardMapper {

    /**
     * 查询本地大屏顶部汇总统计数据。
     *
     * @param bizDate 业务日期，格式 yyyyMMdd，表示要统计哪一天的实例数据
     * @return 汇总统计结果，包含系统数、任务数、成功数、失败数等
     */
    SummaryVO selectSummary(@Param("bizDate") String bizDate,
                            @Param("monthEndView") boolean monthEndView);

    /**
     * 查询本地大屏系统信息卡片。
     *
     * @param bizDate 业务日期，格式 yyyyMMdd，表示查询哪一天的系统卡片统计
     * @param systemName 系统名称，支持模糊查询；为空时查询全部系统
     * @return 系统卡片列表，包含成功、失败、待执行、执行中和总数
     */
    List<SystemDashboardCardVO> selectSystemCards(@Param("bizDate") String bizDate,
                                                  @Param("systemName") String systemName,
                                                  @Param("monthEndView") boolean monthEndView);

    /**
     * 查询本地大屏任务卡片列表。
     *
     * @param bizDate 业务日期，格式 yyyyMMdd，表示查询哪一天的任务实例
     * @return 任务卡片列表
     */
    List<TaskCardVO> selectTaskCards(@Param("bizDate") String bizDate,
                                     @Param("monthEndView") boolean monthEndView);

    /**
     * 查询任务依赖连线关系。
     *
     * @return 连线列表，前端据此绘制任务依赖箭头
     */
    List<LinkVO> selectLinks(@Param("monthEndView") boolean monthEndView);
}
