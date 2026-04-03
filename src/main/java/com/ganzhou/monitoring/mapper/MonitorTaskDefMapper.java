package com.ganzhou.monitoring.mapper;

import com.ganzhou.monitoring.entity.MonitorTaskDef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MonitorTaskDefMapper {

    /**
     * 根据任务编码查询任务定义。
     *
     * @param taskCode 任务编码
     * @return 任务定义
     */
    MonitorTaskDef selectByTaskCode(@Param("taskCode") String taskCode);
}
