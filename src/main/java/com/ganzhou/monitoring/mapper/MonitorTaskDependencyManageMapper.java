package com.ganzhou.monitoring.mapper;

import com.ganzhou.monitoring.entity.MonitorTaskDependency;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 任务依赖关系管理 Mapper。
 */
@Mapper
public interface MonitorTaskDependencyManageMapper {

    List<MonitorTaskDependency> selectByTaskCode(@Param("taskCode") String taskCode);

    int deleteByTaskCode(@Param("taskCode") String taskCode);

    int batchInsert(@Param("list") List<MonitorTaskDependency> list);
}
