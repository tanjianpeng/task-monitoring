package com.ganzhou.monitoring.mapper;

import java.util.List;

import com.ganzhou.monitoring.entity.MonitorTaskInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 任务实例 Mapper。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface MonitorTaskInstanceMapper {

    /**
     * 查询指定业务日期、任务编码、运行批次下的任务实例。
     *
     * @param bizDate 业务日期，格式 yyyyMMdd，表示查询哪一天的任务实例
     * @param taskCode 任务编码，表示查询哪个任务
     * @param runNo 运行批次，表示第几次执行或重跑
     * @return 任务实例，不存在时返回 null
     */
    MonitorTaskInstance selectByBizDateAndTaskCodeAndRunNo(@Param("bizDate") String bizDate,
                                                           @Param("taskCode") String taskCode,
                                                           @Param("runNo") Integer runNo);

    /**
     * 新增任务实例。
     *
     * @param instance 任务实例实体，包含计划时间、执行状态、耗时等快照数据
     * @return 影响行数
     */
    int insert(MonitorTaskInstance instance);

    /**
     * 根据主键更新任务实例。
     *
     * @param instance 任务实例实体，id 表示更新哪条记录，其余字段表示最新状态
     * @return 影响行数
     */
    int updateById(MonitorTaskInstance instance);

    /**
     * 按主键刷新实例的计划信息。
     * 仅用于每日初始化时同步任务定义上的计划时间、预计结束时间等静态快照字段。
     *
     * @param instance 任务实例实体，id 表示要刷新哪条实例，计划相关字段表示最新模板值
     * @return 影响行数
     */
    int updatePlanFieldsById(MonitorTaskInstance instance);

    /**
     * 查询某个任务在指定业务日期下当前最大的重跑次数。
     *
     * @param bizDate 业务日期，格式 yyyyMMdd，表示统计哪一天的重跑批次
     * @param taskCode 任务编码，表示统计哪个任务的最大运行批次
     * @return 最大重跑次数，不存在时返回 null
     */
    Integer selectMaxRunTimes(@Param("bizDate") String bizDate,
                              @Param("taskCode") String taskCode);

    /**
     * 查询实例表中最新一条业务日期。
     * 用途：本地大屏查询优先按实例表最新业务日期取数，避免再依赖字典配置。
     *
     * @return 最新业务日期，格式 yyyyMMdd；实例表为空时返回 null
     */
    String selectLatestBizDate();

    /**
     * 按指定业务日期列表统计任务成功实例的平均耗时，单位分钟。
     * 用途：平日按近 30 个非月底业务日统计，月底按往前 6 个月底业务日统计。
     *
     * @param taskCode 任务编码，表示统计哪个任务
     * @param bizDateList 需要参与统计的业务日期列表，格式 yyyyMMdd
     * @return 平均耗时（分钟）
     */
    Integer selectHistoricalAvgCostMinutesByBizDateList(@Param("taskCode") String taskCode,
                                                        @Param("bizDateList") List<String> bizDateList);
}
