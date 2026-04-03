package com.ganzhou.monitoring.mapper;

import com.ganzhou.monitoring.dto.TaskEventVO;
import com.ganzhou.monitoring.entity.MonitorTaskEvent;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MonitorTaskEventMapper {

    /**
     * 插入事件流水。
     */
    int insert(MonitorTaskEvent event);

    /**
     * 查询任务事件流水，通常用于任务详情弹窗。
     */
    List<TaskEventVO> selectByBizDateAndTaskCode(@Param("bizDate") String bizDate,
                                                 @Param("taskCode") String taskCode);
}
