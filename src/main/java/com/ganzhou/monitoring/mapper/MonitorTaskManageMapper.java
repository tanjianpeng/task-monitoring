package com.ganzhou.monitoring.mapper;

import com.ganzhou.monitoring.entity.MonitorTaskDef;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 任务主数据管理 Mapper。
 */
@Mapper
public interface MonitorTaskManageMapper {

    List<MonitorTaskDef> selectAll();

    MonitorTaskDef selectByCode(@Param("taskCode") String taskCode);

    int insert(MonitorTaskDef task);

    int updateByCode(MonitorTaskDef task);

    int deleteByCode(@Param("taskCode") String taskCode);

    int countBySystemCode(@Param("systemCode") String systemCode);
}
