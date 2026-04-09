package com.ganzhou.monitoring.mapper;

import java.util.List;

import com.ganzhou.monitoring.entity.MonitorTaskDef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 任务主数据管理 Mapper。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface MonitorTaskManageMapper {

    /**
     * 查询全部任务主数据。
     *
     * @return 任务列表，供管理页维护和大屏初始化使用
     */
    List<MonitorTaskDef> selectAll();

    /**
     * 按任务编码查询单个任务。
     *
     * @param taskCode 任务编码，表示要查看或维护哪条任务
     * @return 任务定义
     */
    MonitorTaskDef selectByCode(@Param("taskCode") String taskCode);

    /**
     * 新增任务主数据。
     *
     * @param task 任务实体，包含任务名称、时间、负责人等定义信息
     * @return 影响行数
     */
    int insert(MonitorTaskDef task);

    /**
     * 按任务编码更新任务主数据。
     *
     * @param task 任务实体，taskCode 表示要更新哪条任务记录
     * @return 影响行数
     */
    int updateByCode(MonitorTaskDef task);

    /**
     * 按任务编码删除任务主数据。
     *
     * @param taskCode 任务编码，表示要删除的任务
     * @return 影响行数
     */
    int deleteByCode(@Param("taskCode") String taskCode);

    /**
     * 统计某个系统下挂了多少任务。
     *
     * @param systemCode 系统编码，表示要统计哪个系统下的任务数量
     * @return 任务数量
     */
    int countBySystemCode(@Param("systemCode") String systemCode);
}
