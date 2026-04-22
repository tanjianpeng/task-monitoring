package com.ganzhou.monitoring.mapper;

import java.util.List;

import com.ganzhou.monitoring.entity.MonitorTaskDef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 任务定义 Mapper。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface MonitorTaskDefMapper {

    /**
     * 根据任务编码查询任务定义。
     *
     * @param taskCode 任务编码
     * @return 任务定义
     */
    MonitorTaskDef selectByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 查询全部启用中的任务定义。
     *
     * @return 启用任务列表，主要用于每日 0 点预生成任务实例
     */
    List<MonitorTaskDef> selectActiveTasks(@Param("monthEndView") boolean monthEndView);

}
