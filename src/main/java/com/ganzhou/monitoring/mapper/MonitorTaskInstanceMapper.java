package com.ganzhou.monitoring.mapper;

import com.ganzhou.monitoring.entity.MonitorTaskInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MonitorTaskInstanceMapper {

    /**
     * 查询指定业务日期、任务编码、运行批次下的任务实例。
     */
    MonitorTaskInstance selectByBizDateAndTaskCodeAndRunNo(@Param("bizDate") String bizDate,
                                                           @Param("taskCode") String taskCode,
                                                           @Param("runNo") Integer runNo);

    /**
     * 新增任务实例。
     */
    int insert(MonitorTaskInstance instance);

    /**
     * 根据主键更新任务实例。
     */
    int updateById(MonitorTaskInstance instance);
}
